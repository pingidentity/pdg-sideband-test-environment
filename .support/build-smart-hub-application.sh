#!/bin/bash
echo "Building smart-hub-application. Please wait a few moments..."
(cd .support/smart-hub-application && mvn clean package && \
  cp target/smart-hub-application-1.0.0-SNAPSHOT.jar ../../smart-hub/smart-hub.jar && \
  cp config.yml ../../smart-hub)
