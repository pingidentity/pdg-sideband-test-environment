#!/bin/bash
if [ ! -f .env ]; then
  echo "A .env file is needed to run the application."
  echo "Please run 'cp env-template.txt .env', and modify the result if necessary."
  exit 1
fi
. .env
echo "Running simulated-sideband-adapter..."
(cd .support/simulated-sideband-adapter && docker run --network host -it \
  --env "PDG_SIDEBAND_REQUEST_CONTEXT_METHOD=$PDG_SIDEBAND_REQUEST_CONTEXT_METHOD" \
  $(docker build -q .)) 
