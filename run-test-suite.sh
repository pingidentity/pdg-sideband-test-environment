#!/bin/sh
if [ ! -f ./.env ]; then
  echo "A .env file is needed to run the application."
  echo "Please run 'cp env-template.txt .env', and modify the result if necessary."
  exit 1
fi
. ./.env
echo "Running test suite..."
(cd test-suite && docker run --network host -it \
  --env "PDG_SIDEBAND_REQUEST_CONTEXT_METHOD=$PDG_SIDEBAND_REQUEST_CONTEXT_METHOD" \
  --env "PDG_SIDEBAND_DG_PORT=$PDG_SIDEBAND_DG_PORT" \
  --env "PDG_SIDEBAND_SMART_HUB_PORT=$PDG_SIDEBAND_SMART_HUB_PORT" \
  "$(docker build -q .)")

