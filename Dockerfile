FROM maven:3.9.7-eclipse-temurin-21-alpine@sha256:8b762a139e07e874e3830521d97bafaf963cce6bda92afe9fb532def5d011404 AS builder

WORKDIR /home/build
COPY . .

RUN ["mvn", "clean", "--no-transfer-progress", "package"]

FROM eclipse-temurin:21-jre-alpine@sha256:3f716d52e4045433e94a28d029c93d3c23179822a5d40b1c82b63aedd67c5081 AS final

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
