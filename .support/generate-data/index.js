const jsf = require('json-schema-faker');
const fs = require('fs');

const schemaDir = `${__dirname}/schema`;
const outputDir = `${__dirname}/output`;

const hubSchema = JSON.parse(fs.readFileSync(`${schemaDir}/Hub.json`));

const minPersons = 5;

const knownHomeUuid = "5b99ccf0-a164-4472-baae-6243768f0a5b";
const knownDevice = {
  id: "83f6e6ae-96fe-4ba9-9480-88a3a33a7add",
  name: "Josh's luxury sedan",
  type: "vehicle",
  product: "Carnivale",
  vendor: "Panaphonics",
  controls: [{
    id: "1da1a20f-6707-40b3-a2af-a6bc9054b640",
    name: "power",
    status: "off",
    value: "88000",
    callsRemaining: 0,
    readOnly: false
  }]
};
const knownPersonUuid = "7d9fc465-ec1a-460c-ab38-435471f4918b";
const bannedPersonUuid = "705fd0eb-380c-4007-a2da-900ea8d58987";
const bannedPersonUsername = "l33t_haxor";

/**
 * Selects a random index from an array
 */
const selectRandomIndex = function(arr) {
  return Math.floor(Math.random() * 10) % arr.length;
}

/**
 * Selects a random entity from an array.
 *
 * @param entities An array of entities.
 * @param notIn (optional) An array of ids that should not be selected.
 */
const selectRandom = function(entities, notIn) {
  let selection = entities[selectRandomIndex(entities)];
  while ((notIn || []).indexOf(selection.id) !== -1) {
    selection = entities[selectRandomIndex(entities)];
  }
  return selection;
}

/**
 * After randomized data has been generated, insert
 * constant pieces of data at known locations for the
 * Postman tests to succeed without manual data modification.
 */
const applyConstantData = function(hub) {
    const persons = Object.values(hub.persons);

    // Change a user to have a known person ID
    const randomPerson = selectRandom(persons);
    delete hub.persons[randomPerson.id];
    randomPerson.id = knownPersonUuid;
    hub.persons[randomPerson.id] = randomPerson;

    // Change a user to have a banned username
    const bannedPerson = selectRandom(persons, [ knownPersonUuid ]);
    delete hub.persons[bannedPerson.id];
    bannedPerson.id = bannedPersonUuid;
    bannedPerson.username = bannedPersonUsername;
    hub.persons[bannedPerson.id] = bannedPerson;

    // Randomly assign people to homes
    hub.homes.forEach(home => {
      home.members = [];
      const numMembers = Math.max(1, selectRandomIndex(persons));
      for (let i = 0; i < numMembers; i++) {
        home.members.push(selectRandom(persons, home.members.map(member => member.id)));
      }
    });

    // Randomly place a known device in a home
    const randomHome = selectRandom(hub.homes);
    randomHome.id = knownHomeUuid;
    randomHome.devices[selectRandomIndex(randomHome.devices)] = knownDevice;
    return hub;
}

jsf.extend('faker', () => {
  const faker = require('faker')
  faker.custom = {

    // Create at least minPersons persons and restructure the data so that it is a mapping
    // of person IDs to person objects
    persons: () => {
      const generated = {};
      const numToGenerate = Math.max(minPersons, Math.floor(Math.random() * 10));
      for (let i = 0; i < numToGenerate; i++) {
        const uuid = faker.random.uuid();
        const newPerson = faker.helpers.userCard();
        newPerson.id = uuid;
        generated[uuid] = newPerson;
      }
      return generated;
    }
  };
  return faker;
});

jsf.resolve(hubSchema, null, schemaDir)
  .then(applyConstantData)
  .then(JSON.stringify)
  .then(console.log)
  .catch(console.err);

