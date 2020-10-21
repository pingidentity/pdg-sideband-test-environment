#!/bin/bash
set -euo pipefail

echo "Pushing to GitHub..."
git remote add github \
  "https://${GITHUB_USER}:${GITHUB_ACCESS_TOKEN}@github.com/pingidentity/pdg-sideband-test-environment.git" >/dev/null 2>&1 || echo "github remote already exists. Skipping add."
git push github main
git push github "$CI_COMMIT_TAG"
exit 0
