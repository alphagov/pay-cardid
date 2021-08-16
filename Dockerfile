FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine@sha256:b3e75856673921664c3d41149dc075720421e5ec9241ec3d146d3ba27751a6b9

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

COPY data/sources/ /app/data/
COPY target/*.yaml /app/
COPY target/pay-*-allinone.jar /app/

ENTRYPOINT ["tini", "-e", "143", "--"]

CMD exec java $JAVA_OPTS -jar ./pay-*-allinone.jar server ./*.yaml
