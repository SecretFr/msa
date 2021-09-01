# 클라우드 MSA 토이 프로젝트

## rabbimq config source
[소스코드](https://github.com/SecretFr/ecommerce)

## 상용 클라우드를 이용한 어플리케이션 배포
[소스코드](https://github.com/SecretFr/msa)
* 토이 프로젝트를 통한 msa 어플리케이션 실습
    - Docker간 네트워크
    - 도커 레지스트리
    
* 토이 프로젝트 배포 작업

## 도커 간 네트워크
* Bridge network
* Host network
* None network
* Container network
* Overlay network

### 브릿지 네트워크 생성
> docker network create [network name]

### 브릿지 연결과 해제
* 브릿지 네트워크를 새로 connect하면 기존의 브릿지와 함께 연결되어 있음
* docker network connect my-network service-discovery
* docker network disconnect bridge service-discovery

#### mariadb 컨테이너 외부 컨테이너(서비스 디스커버리)에서 접속
> 매번 바뀌는 브릿지 네트워크 ip를 접속하려 할때 이 ip 주소를 쓰는 것 대신 컨테이너 name을 작성해 접속
* mysql -hmariadb1 -uroot -p

## 도커 레지스트리
* private 저장 장소

#### 이미지 다운 및 실행
* docker pull registry
* docker run -d -p 5000:5000 --restart always --name registry registry:latest

#### 레지스트리 저장
* docker image tag {hub-id}/mydb localhost:5000/mydb:latest

#### 레지스트리 푸시
* docker push localhost:5000/mydb:latest

#### 레지스트리 목록 확인
* curl -X GET http://localhost:5000/v2/_catalog

## 외부 클라우드에 있는 레지스트리 -> 도커로 연결
* 인증받은 사용자들만 사용할 수 있음!
1. aws - ecr 서비스 - 레파지토리 생성
2. 생성된 레파지토리 주소 복사
3. docker tag {hub-id}/service-discovery {IAM사용자번호}.dkr.ecr.{지역코드}.amazonaws.com

### ECR (aws 클라우드 레파지토리)
* aws에 있는 ECR 비밀번호 코드를 들고 도커 ECR 레파지토리 접속
> aws ecr get-login-password --region {지역코드} | docker login --username AWS --password-stdin {IAM사용자번호}.dkr.ecr.{지역코드}.amazonaws.com

#### 도커 -> 클라우드 레파지토리(ECR) 이미지 업로드
* Tag 이미지 만들기
> docker tag {hub-id}/mydb {aws-iam-id}.dkr.ecr.{aws-region}.amazonaws.com/{aws-user-id}/mydb

* 클라우드에 올리기
> docker push {aws-iam-id}.dkr.ecr.{aws-region}.amazonaws.com/{aws-user-id}/mydb

* 클라우드 레파지토리에서 도커로 pull
> docker pull {aws-iam-id}.dkr.ecr.{aws-region}.amazonaws.com/{aws-user-id}/mydb

* Mac M1 호환 가능하게 image build 및 푸쉬하기
> docker buildx build --platform linux/amd64,linux/arm64 -t linkclean/config-service:1.0 -f ./Dockerfile --push .

## 토이 프로젝트 배포 작업
* kafka - zookeeper
* zipkin: 서비스 호출 추적(Trace ID, Span ID)
* Prometheus: 모니터링, metrix 수집
* Grafana: 모니터링, 데이터 시각화
* Service Discovery Eureka
* API Gateway
* Configuration(with Git) -> Rabbit MQ
* Service
    - User
    - Order
    - Catalog
* MariaDB
    - kafka를 통해 데이터를 보냈다.
    - kafka connect -> sink connect로 데이터 연결

> 이번 작업에서 kafka connect는 포함하지 않는다. 

### 실행 순서
> 모두 같은 docker network로 묶이도록 생성해야 한다.

0. RabbitMQ for Configuration
1. Configuration
2. Eureka
3. API Gateway
4. User, Catalog, Orders
5. MariaDB
6. Kafka & zipkin

### 도커 브릿지 네트워크 생성
> Bridge Network를 활용할 예정
```
docker network create eccommerce-network
docker network ls
```
* docker network create --gateway 172.18.0.1 --subnet 172.18.0.0/16 ecommerce-network

#### 각 서비스 별 포트 번호
```
rabbitmq            172.18.0.2:5672:5672
config-service      172.18.0.3:8888:8888
service-discovery   172.18.0.4:8761:8761
apigateway-service  172.18.0.5:8000:8000
mariadb             172.18.0.6:3306:3306
```

#### 각 서비스 별 실행
* RabbitMQ
```
docker run -d --name rabbitmq --network ecommerce-network -p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:management
```
* Config Service
```
docker run -d --name config-service --network ecommerce-network -p 8888:8888 -e "spring.rabbitmq.host=rabbitmq" -e "spring.profiles.active=git" namgonkim/config-servie:1.0
```
* Service Discovery
```
docker run -d --name service-discovery --network ecommerce-network -p 8761:8761 -e "spring.cloud.config.uri=http://config-service:8888" namgonkim/service-discovery:1.0
```
* ApiGateway Service
```
docker run -d --name apigateway-service --network ecommerce-network -p 8000:8000 -e "spring.cloud.config.uri=http://config-service:8888" -e "spring.rabbitmq.host=rabbitmq" -e "eureka.client.serviceUrl.defaultZone=http://service-discovery:8761/eureka/" namgonkim/apigateway-service:1.0
```
* MariaDB
```
docker run -d --name mariadb --network ecommerce-network -p 3306:3306 wodndl895/mydb:2.0

```
* kafka
```
kafka 이미지 pull
docker-compose-single-broker.yml 파일 수정
docker-compose-single-broker.yml 실행
```
* zipkin
```
docker run -d -p 9411:9411 --name zipkin --network ecommerce-network openzipkin/zipkin
```
* User Service
```
docker run -d --name user-service --network ecommerce-network -e "spring.cloud.config.uri=http://config-service:8888" -e "eureka.client.serviceUrl.defaultZone=http://service-discovery:8761/eureka/" -e "spring.rabbitmq.host=rabbitmq" -e "spring.zipkin-base-url=http://zipkin:9411" -e "logging.file=/api-logs/users-ms.log" -p 50001:50001 linkclean/user-service:1.0
```
* Order Service
```
docker run -d --network ecommerce-network --name order-service -e "spring.zipkin.base-url=http://zipkin:9411" -e "eureka.client.serviceUrl.defaultZone=http://service-discovery:8761/eureka/" -e "spring.datasource.url=jdbc:mariadb://mariadb:3306/mydb" -e "logging.file=/api-logs/orders-ws.log" linkclean/order-service:1.0
```
* Catalog Service
```
docker run -d --network ecommerce-network  --name catalog-service -e "eureka.client.serviceUrl.defaultZone=http://service-discovery:8761/eureka/" -e "logging.file=/api-logs/catalogs-ws.log" linkclean/catalog-service:1.0
```

#### 해야할 일
1. mariadb 이미지 생성  
- 이미지 생성할 dockerfile
```Dockerfile
# mariadb -> mydb-image
# ~/dbdata -> copy -> init db
# environments -> root pw, db name
FROM mariadb:latest

ENV MARIADB_ROOT_PASSWORD 1234
ENV MARIADB_DATABASE mydb

COPY ./shared_dbdata /var/lib/mysql

EXPOSE 3306

CMD ["mysqld"]
```
- 이미지 build
> docker buildx build -t namgonkim/mydb:2.0 -f ./Dockerfile-db .
- orders table 생성작업
```sql
create table orders (id int auto_increment primary key,
user_id varchar(50) not null,
product_id varchar(20) not null,
order_id varchar(50) not null,
qty int default 0,
unit_price int default 0,
total_price int default 0,
created_at datetime default now()
)
```

2. jpa사용, kafka connect 코드 삭제
* order-service

### docker-compose
* 만들어진 이미지파일을 가지고 Container 실행 방법을 정의하고 실행하는 도구
* Docker 커맨드 or 복잡한 설정을 쉼게 관리하기 위한 도구

- docker-compose commanc
> docker-compose -f docker-compose.yml up {appname}

> docker-compose down {appname}

> docker-compose stop {appname}
- docker-compose yaml 생성
```docker-compose.yml
version: "3.9"
services: 
  myweb:
    image: mynode:latest
    ports: 
      - "8080:8080"
    depends_on: 
      - mydb
  mydb:
    image: mariadb:latest
    volumes: 
      - ../dbdata:/var/lib/mysql
    environment: 
      MARIADB_ROOT_PASSWORD: test1357
      MARIADB_DATABASE: mydb
    ports: 
      - "23306:3306"
```
* docker-compose-single-broker.yml
```docker-compose-single-broker.yml
version: '2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
      - "2181:2181"
    networks: 
      my-network:
        ipv4_address: 172.18.0.100
  kafka:
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 172.18.0.101
      KAFKA_CREATE_TOPICS: "test:1:1"
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    depends_on: 
      - zookeeper
    networks: 
      my-network:
        ipv4_address: 172.18.0.101

networks: 
  my-network:
    name: ecommerce-network
```
* 위 설정 안될시 bitnami kafka 이미지 이용
```bitnami
version: '2'

networks:
  my-network:
    name: ecommerce-network

services:
  zookeeper:
    image: 'bitnami/zookeeper:latest'
    ports:
      - "2181:2181"
    environment:
      ALLOW_ANONYMOUS_LOGIN: "yes"
    networks:
      my-network:
        ipv4_address: 172.18.0.100
  kafka:
    image: 'bitnami/kafka:latest'
    ports:
      - "9092:9092"
    environment:
      ALLOW_PLAINTEXT_LISTENER: "yes"
      KAFKA_CFG_ZOOKEEPER_CONNECT: "zookeeper:2181"
    depends_on: 
      - zookeeper
    networks:
      my-network:
        ipv4_address: 172.18.0.101
```

* zombie process 삭제
> http://127.0.0.1:8761/eureka/apps/USER-SERVICE/cebdfc598db9:ffaa5472fdfe640779c6038e92ae60e0

* mariadb-client
> apt-get install mariadb-client
