#!/bin/bash

# Generate data for consumption by the PingDataGovernance Side Test Environment.
set -e pipefail

echo "Generating data..."

# Run the data generation and send the output to two locations
(cd .support/generate-data && npm install >/dev/null 2>&1 && node index.js \
  | tee ../../smart-hub-application/hub.json \
  > ../extract-users/hub.json \
  && echo "Successfully generated data.")

echo "Extracting users..."

# Extract the users from the data and place it in the server profile
(cd .support/extract-users && ./run.sh hub.json \
  > ../../server-profiles/pingdatagovernance/instance/users.json \
  && echo "Successfully extracted users.")

echo "Data generation/user extraction successful."

