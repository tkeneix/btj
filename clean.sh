#!/bin/bash

source ./btj_setenv.sh

rm -rf ${APP_HOME}/lib/*
rm -rf ${APP_HOME}/btj_core/bin/*
rm -rf ${APP_HOME}/btj_service/bin/*
rm -rf ${APP_HOME}/btj_user/bin/*
rm -rf ${APP_HOME}/LOG
rm -rf ${APP_HOME}/TALLY_LOG

echo "Clean completed."