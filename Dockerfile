FROM govukpay/openjdk:8-jre-alpine

RUN apk update
RUN apk upgrade

RUN apk add --no-cache bash netcat-openbsd 

ENV PORT 8080
ENV ADMIN_PORT 8081

EXPOSE 8080
EXPOSE 8081

WORKDIR /app

ADD target/*.yaml /app/
ADD target/pay-*-allinone.jar /app/
COPY data/* /app/data/
RUN ls -a /app/data/*
ADD docker-startup.sh /app/docker-startup.sh

CMD bash ./docker-startup.sh
