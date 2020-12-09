#!/bin/sh

# Uses Maven to build the smart-hub-application JAR and copies it to the staging location within the repository. Must
# be run from the project root directory.
#
# Usage:
# .support/build-smart-hub-application.sh
#
echo "Building smart-hub-application. Please wait a few moments..."
(cd .support/smart-hub-application && mvn clean package && \
  cp target/smart-hub-application-1.0.0-SNAPSHOT.jar ../../smart-hub/smart-hub.jar && \
  cp config.yml ../../smart-hub)
