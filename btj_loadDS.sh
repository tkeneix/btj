#!/bin/bash
source ./btj_setenv.sh

export CLASSPATH=lib/btj.jar:lib/btj_service.jar:lib/btj_user.jar
${JAVA_HOME}/bin/java -server -Xms128m -Xmx512m btj.client.Command Server1 loadDataSet candle candle_nk225_d1.properties
