const newman = require('newman');

const collectionName = process.argv[2] || 'sideband-adapter';

// Add all UNIX environment variables starting with "PDG_SIDEBAND_",
// but convert them to use hyphens and lowercase.
const envVar = Object.keys(process.env)
  .filter(key => key.startsWith('PDG_SIDEBAND_'))
  .map(key => ({ 
    key: key.replace(/^PDG_SIDEBAND_/, '').replace(/_/g, '-').toLowerCase(),
    value: process.env[key]
  }))
  .reduce((acc, keyValueObject) => acc.concat([ keyValueObject ]), []);

newman.run({
  collection: require(`./${collectionName}.postman_collection.json`),
  reporters: [ 'cli' ],
  reporter: {
    cli: {
      noBanner: true,
      noSummary: true
    }
  },
  insecure: true,
  noBanner: true,
  noSummary: true,
  envVar: envVar
}, err => {
  if (err) {
    throw err;
  }
});
