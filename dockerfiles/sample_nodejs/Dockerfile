# OS, Middleware, Platform, WAS
# ex) ubuntu, mysql, mariadb, tomcat, jetty, jboss, express
# platform: jdk, python, nodejs
# FROM alpine
FROM node:14.17-alpine3.14

# workdir myaqpp ->  copy

RUN mkdir /myapp
WORKDIR /myapp

COPY ./package.json ./package.json
COPY ./index.js ./index.js

# RUN ["npm", "install"]
RUN npm install

# install curl
# RUN ["apk", "no-cache", "add", "curl"]
RUN apk --no-cache add curl

EXPOSE 8080

CMD ["npm", "start"]
