#!/bin/sh

# Generates data for consumption by the PingDataGovernance Side Test Environment by building and running the NodeJS
# Docker image, copying the output to the staging location and a `jq` Docker image to transform the output for use by
# the ExampleTokenResourceLookupMethod. Must be run from the project root directory.
#
# Usage:
#   .support/generate.sh
#

echo "Generating data..."

# Run the docker data generation and send the output to two locations
(cd .support/generate-data && docker run -it $(docker build -q .) \
  | tee ../../smart-hub/hub.json \
  > ../extract-users/hub.json && echo "Successfully generated data.")

echo "Extracting users..."

# Extract the users from the data and place it in the server profile
(cd .support/extract-users && docker run -it $(docker build -q .) \
  > ../../server-profiles/pingdatagovernance/instance/users.json \
  && echo "Successfully extracted users.")

echo "Data generation/user extraction successful."

