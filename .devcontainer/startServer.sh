#!/bin/bash

tar -xzf /workspace/microsoft-autodev-0.7.0.tgz -C /workspace/
node /workspace/package/dist/src/java-upgrade/webServer/server.js

exit 0
