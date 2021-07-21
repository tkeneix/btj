#!/bin/bash
source ./btj_setenv.sh

cd ${APP_HOME}/btj_core
${JAVA_HOME}/bin/javac -d bin `find ./* -type f -name *.java -print | xargs`
${JAVA_HOME}/bin/rmic -classpath bin -d bin -iiop gridsample.InnerService gridsample.InnerServiceImpl
cd ${APP_HOME}/btj_core/bin
${JAVA_HOME}/bin/jar cf ${APP_HOME}/lib/btj.jar *

cd ${APP_HOME}/btj_service
${JAVA_HOME}/bin/javac -d bin -classpath ${APP_HOME}/btj_core/bin  `find ./* -type f -name *.java -print | xargs`
${JAVA_HOME}/bin/rmic -classpath bin:${APP_HOME}/lib/btj.jar -d bin -iiop btj.service.BackTestService btj.service.BackTestServiceImpl
cd ${APP_HOME}/btj_service/bin
${JAVA_HOME}/bin/jar cf ${APP_HOME}/lib/btj_service.jar *

cd ${APP_HOME}/btj_user
${JAVA_HOME}/bin/javac -d bin -classpath ${APP_HOME}/btj_core/bin:${APP_HOME}/btj_service/bin `find ./* -type f -name *.java -print | xargs`
cd ${APP_HOME}/btj_user/bin
${JAVA_HOME}/bin/jar cf ${APP_HOME}/lib/btj_user.jar *

cd ${APP_HOME}

echo "Build completed. (Ignore the Notes)"