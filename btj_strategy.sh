#!/bin/bash
source ./btj_setenv.sh

export CLASSPATH=lib/btj.jar:lib/btj_service.jar:lib/btj_user.jar
${JAVA_HOME}/bin/java -server -Xms128m -Xmx128m btj.strategy.$1 Server1
