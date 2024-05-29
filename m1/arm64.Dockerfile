FROM maven:3.9.6-eclipse-temurin-22@sha256:6999511d285ea595b0eb59dc0514dfad50534118ba400a40138a15653b54e44c AS builder

WORKDIR /home/build
COPY . .

RUN ["mvn", "clean", "--no-transfer-progress", "package"]

FROM eclipse-temurin:11-jre@sha256:f31717a7a4c0d7cc9e50008544d269c90d6894dd70cdcbb28da8060913fcf8ad AS final

RUN ["apt-get", "update"]
RUN ["apt-get", "upgrade", "-y"]
RUN ["apt-get", "install", "-y", "tini"]
RUN ["apt-get", "clean"]

ARG DNS_TTL=15

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

RUN echo networkaddress.cache.ttl=$DNS_TTL >> "$JAVA_HOME/conf/security/java.security"

ENV PORT 8080
ENV ADMIN_PORT 8081
ENV JAVA_OPTS -Xms1500m -Xmx1500m

EXPOSE 8080
EXPOSE 8081

WORKDIR /app

COPY --from=builder /home/build/target/*.yaml .
COPY --from=builder /home/build/target/pay-*-allinone.jar .

ENTRYPOINT ["tini", "-e", "143", "--"]

CMD exec java $JAVA_OPTS -jar ./pay-*-allinone.jar server ./*.yaml
