FROM govukpay/openjdk:adoptopenjdk-jre-11.0.3_7-alpine

RUN apk --no-cache upgrade

RUN apk --no-cache add bash

ENV PORT 8080
ENV ADMIN_PORT 8081

EXPOSE 8080
EXPOSE 8081

WORKDIR /app

COPY docker-startup.sh /app/docker-startup.sh
COPY data/sources/ /app/data/
COPY target/*.yaml /app/
COPY target/pay-*-allinone.jar /app/

CMD bash ./docker-startup.sh
