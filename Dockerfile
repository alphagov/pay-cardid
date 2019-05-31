# adoptopenjdk/openjdk11:jre-11.0.3_7-alpine
FROM adoptopenjdk/openjdk11@sha256:eaa182283f19d3f0ee0c6217d29e299bb4056d379244ce957e30dcdc9e278e1e

RUN ["apk", "--no-cache", "upgrade"]

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

CMD exec java $JAVA_OPTS -jar ./pay-*-allinone.jar server ./*.yaml
