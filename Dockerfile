FROM maven:3.9.8-eclipse-temurin-22-alpine@sha256:63dc926762f2cf571b11d421e3c9281b8e8ae81e4ffd32cb48eb9ca8068e39a9 AS builder

WORKDIR /home/build
COPY . .

RUN ["mvn", "clean", "--no-transfer-progress", "package"]

FROM eclipse-temurin:21-jre-alpine@sha256:4e9ab608d97796571b1d5bbcd1c9f430a89a5f03fe5aa6c093888ceb6756c502 AS final

RUN ["apk", "--no-cache", "upgrade"]
RUN ["apk", "--no-cache", "add", "tini"]

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
