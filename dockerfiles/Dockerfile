FROM ubuntu:16.04

RUN mkdir /mydata
RUN echo "Hello, Docker!"

WORKDIR /mydata

COPY test.txt ./test.txt

ADD test.sh /
RUN chmod +x /test.sh
ENTRYPOINT /test.sh