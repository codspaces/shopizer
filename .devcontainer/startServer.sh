#!/bin/bash

export JAVA_HOME=/usr/local/sdkman/candidates/java/current
export JAVA8HOME=/usr/local/sdkman/candidates/java/8.0.382-tem
export JAVA11HOME=/usr/local/sdkman/candidates/java/11.0.20-tem
export JAVA17HOME=/usr/local/sdkman/candidates/java/17.0.8-tem
export JAVA21HOME=/usr/local/sdkman/candidates/java/21.0.4-tem

export SDKMAN_DIR=/usr/local/sdkman
export PATH="${JAVA11HOME}/bin:${SDKMAN_DIR}/candidates/maven/current/bin:${PATH}"

node /workspace/package/dist/src/java-upgrade/webServer/server.js

exit 0
