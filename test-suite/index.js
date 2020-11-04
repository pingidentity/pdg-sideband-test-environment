const newman = require('newman');

const config = {
  dgPort: process.env["PDG_SIDEBAND_DG_PORT"],
  backendPort: process.env["PDG_SIDEBAND_SMART_HUB_PORT"],
  requestContextMethod: process.env["PDG_SIDEBAND_REQUEST_CONTEXT_METHOD"],
}

console.debug(config);

const dgBaseUrl = `https://localhost:${config.dgPort}`;
const backendBaseUrl = `https://localhost:${config.backendPort}`;

newman.run({
  collection: require('./test-suite.postman_collection.json'),
  reporters: [ 'cli' ],
  insecure: true,
  envVar: [
    { key: "pdg-base-url", value: dgBaseUrl },
    { key: "backend-base-url", value: backendBaseUrl },
    { key: "request-context-method", value: config.requestContextMethod }
  ]
}, err => {
  if (err) {
    throw err;
  }
});
