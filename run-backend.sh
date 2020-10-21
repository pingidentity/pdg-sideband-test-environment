#!/bin/sh
if [ ! -f .env ]; then
  echo "A .env file is needed to run the application."
  echo "Please run 'mv env-template.txt .env', and modify the result if necessary."
  exit 1
fi
. .env
echo "Running backend-rest-application..."
(cd backend-rest-application && docker run -p"$PDG_SIDEBAND_BACKEND_PORT":8080 -it $(docker build -q .))
