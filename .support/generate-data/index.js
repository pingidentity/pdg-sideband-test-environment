const jsf = require('json-schema-faker');
const fs = require('fs');

const schemaDir = `${__dirname}/schema`;
const outputDir = `${__dirname}/output`;

const hubSchema = JSON.parse(fs.readFileSync(`${schemaDir}/Hub.json`));

jsf.extend('faker', () => {
  const faker = require('faker')
  faker.custom = {

    // Add uuids to users
    persons: () => {
      const generated = {};
      const numToGenerate = Math.max(5, Math.floor(Math.random() * 10));
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
  .then(hub => {
    const persons = Object.values(hub.persons);
    // Change the first user to have a banned username
    persons[0].username = "l33t_haxor";

    // Randomly assign people to homes
    hub.homes.forEach(home => {
      home.members = [];
      const numMembers = Math.max(1, Math.floor(Math.random() * 10)
        % persons.length);
      for (let i = 0; i < numMembers; i++) {
        home.members.push(persons[Math.floor(Math.random() * 10)
          % persons.length]);
      }
    });
    return hub;
  })
  .then(JSON.stringify)
  .then(console.log)
  .catch(console.err);

