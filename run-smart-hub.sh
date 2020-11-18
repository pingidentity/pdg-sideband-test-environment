#!/bin/sh
if [ ! -f ./.env ]; then
  echo "A .env file is needed to run the application."
  echo "Please run 'cp env-template.txt .env', and modify the result if necessary."
  exit 1
fi
. ./.env
echo "Running smart-hub. Please wait a few moments..."
(cd smart-hub && \
  docker run -p"$PDG_SIDEBAND_SMART_HUB_PORT":8080 -it \
  $(docker build -q .))
