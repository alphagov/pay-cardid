FROM eclipse-temurin:11-jre@sha256:07f183ac841f489f986fe991b9e21cdf4ebb43c3fb7fe0cc800250ff9a5b4405

RUN ["apt-get", "update"]
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

COPY data/sources/ /app/data/
COPY target/*.yaml /app/
COPY target/pay-*-allinone.jar /app/

ENTRYPOINT ["tini", "-e", "143", "--"]

CMD exec java $JAVA_OPTS -jar ./pay-*-allinone.jar server ./*.yaml
