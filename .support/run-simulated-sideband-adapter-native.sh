#!/bin/bash

set -e pipefail

echo "Running simulated-sideband-adapter..."

# Run the simulated-sideband-adapter
(cd .support/simulated-sideband-adapter && npm install >/dev/null 2>&1 && npm start)

