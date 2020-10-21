#!/bin/bash
set -euo pipefail

echo "Pushing to GitHub..."
git remote add github \
  "https://${GITHUB_USER}:${GITHUB_ACCESS_TOKEN}@github.com/pingidentity/pdg-sideband-test-environment.git"
git push github "$CI_COMMIT_TAG"
exit 0
