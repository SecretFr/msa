### Docker Network 생성
```
docker network create --gateway 172.18.0.1 --subnet 172.18.0.0/16 ecommerce-network
```

### RabbitMQ 서버 기동
```
docker run -d --name rabbitmq --network ecommerce-network \
    -p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 \
    rabbitmq:management
    (-e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest \)
```

### Configuration Server 기동
```
docker run -d --name config-service --network ecommerce-network \
    -p 8888:8888 \
    -e "spring.rabbitmq.host=rabbitmq" -e "spring.profiles.active=git" \
    edowon0623/config-service:1.0
```

### Service Discovery 기동
```
docker run -d --name service-discovery --network ecommerce-network \
    -p 8761:8761 \
    -e "spring.cloud.config.uri=http://config-service:8888" \
    edowon0623/service-discovery:1.0
```

### Apigateway Service 기동
```
docker run -d --name apigateway-service --network ecommerce-network \
    -p 8000:8000 \
    -e "spring.cloud.config.uri=http://config-service:8888" \
    -e "spring.rabbitmq.host=rabbitmq" \
    -e "eureka.client.serviceUrl.defaultZone=http://service-discovery:8761/eureka/" \
    edowon0623/apigateway-service:1.0
```

### Mariadb Server기동
```
docker run -d --name mariadb --network ecommerce-network \
    -p 13306:3306 edowon0623/mydb:latest
```:wq

