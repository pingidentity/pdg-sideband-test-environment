#!/bin/sh
#
# Runs the test harness. Takes an optional collection name to run. Must be run from the project root.
#
# Arguments:
#   COLLECTION_NAME {Optional} The basename of the collection to run. Uses sideband-adapter by default.
#
# Usage:
#   ./run-test-harness.sh [COLLECTION_NAME]
#
# Example:
#   ./run-test-harness.sh "sideband-api-request-examples"
#
if [ ! -f ./.env ]; then
  echo "A .env file is needed to run the application."
  echo "Please run 'cp env-template.txt .env', and modify the result if necessary."
  exit 1
fi
echo "Building and running test-harness. Please wait a few moments..."
echo
(cd test-harness && \
  docker run \
  --network host -it \
  --env-file ../.env \
  "$(docker build -q .)" \
  -- "$@")

