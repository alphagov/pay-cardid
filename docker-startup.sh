#!/usr/bin/env bash

JAVA_OPTS=${JAVA_OPTS:--Xms1500m -Xmx1500m}
java $JAVA_OPTS -jar *-allinone.jar server *.yaml
