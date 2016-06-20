#!/usr/bin/env bash

if [ "${ENABLE_NEWRELIC}" == "yes" ]; then
  NEWRELIC_JVM_FLAG="-javaagent:/app/newrelic/newrelic.jar"
fi

java -Xms1500m -Xmx1500m ${NEWRELIC_JVM_FLAG} -jar *-allinone.jar server *.yaml
