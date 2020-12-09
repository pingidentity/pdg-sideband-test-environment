#!/bin/sh

# Changes the pingdatagovernance configuration to use the external PDP included in .support/docker-compose.yml. Must be
# run from the project root directory.
#
# Usage:
#   ./.support/use-external-pdp.sh
#
echo "Enabling external PDP mode..."
docker exec pingdatagovernance /opt/out/instance/bin/dsconfig --no-prompt set-policy-decision-service-prop \
  --set pdp-mode:external \
  --set "policy-server:Policy Administration GUI" && echo "Success!" || echo "Failed."

