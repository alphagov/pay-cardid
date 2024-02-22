FROM maven:3.9.5-eclipse-temurin-11@sha256:d698c543af44a1a8e4b2b2f9c1cfe3e009f2398cc7125a3d1204c71ad876800f AS builder

WORKDIR /home/build
COPY . .

RUN ["mvn", "clean", "--no-transfer-progress", "package", "-DskipTests"]

FROM eclipse-temurin:11-jre@sha256:05ee511056c2991a5e60fd6659a8d6aba93163c858ee545413a78b5b9842c4d8 AS final

RUN ["apt-get", "update"]
RUN ["apt-get", "upgrade", "-y"]
RUN ["apt-get", "install", "-y", "tini"]

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

COPY --from=builder /home/build/data/sources /app/data
COPY --from=builder /home/build/target/*.yaml .
COPY --from=builder /home/build/target/pay-*-allinone.jar .

ENTRYPOINT ["tini", "-e", "143", "--"]

CMD exec java $JAVA_OPTS -jar ./pay-*-allinone.jar server ./*.yaml
