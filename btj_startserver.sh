#!/bin/bash
source ./btj_setenv.sh

export CLASSPATH=lib/btj.jar:lib/btj_service.jar
${JAVA_HOME}/bin/java -server -Xms512m -Xmx512m btj.service.BackTestServer Server1
