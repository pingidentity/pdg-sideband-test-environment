#!/bin/bash

set -e pipefail

echo "Running test suite..."

# Run the test suite
(cd test-suite && npm install >/dev/null 2>&1 && node index.js \
  && echo "Test suite execution completed.")

