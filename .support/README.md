# PingDataGovernance Sideband Test Environment Maintainer Guide

## Overview

This document describes features of the pdg-sideband-test-environment relevant to repository maintainers. Sideband
adapter developers should instead refer to the [README.md](../README.md) in the root directory of the project.
This document does not cover the GitLab CI pipeline, which is covered in [.gitlab-ci/](../.gitlab-ci/).
In general, all scripts should be run from the root of the project, and not from the `.support` folder itself.

## smart-hub-application

The smart-hub-application project is a DropWizard project that simulates serving Resources for a simulated smart
device collection. For convenience, we provide a script that builds the project using Maven (3.6.3 at time of writing),
and stages the artifact and configuration to [smart-hub](../smart-hub).

```bash
./support/build-smart-hub-application.sh
```

The smart-hub-application project layout follows
[DropWizard conventions](https://www.dropwizard.io/en/latest/manual/core.html#organizing-your-project). This means that
if there are any data structure changes, corresponding changes must be made in the
  `com.pingidentity.dg.smart_hub.api` Java package.

In addition to DropWizard, the project uses [lombok](https://projectlombok.org/) for developer convenience.

## Policy changes

The pdg-sideband-test-environment server profiles contain policies in two forms, a deployment package 
([smart-hub.sdp](../server-profiles/pingdatagovernance/instance/smart-hub.sdp)) and a policy snapshot
([smart-hub.snapshot](../server-profiles/pingdatagovernancepap/policies/smart-hub.snapshot)). To modify the policies,
a maintainer will need an instance of the PingDataGovernance Policy Administration GUI. A `docker-compose.yml` file, 
separate from the one in the project root, has been provided in this directory that includes pingdatagovernance, 
pingdatagovernancepap, and pingdataconsole. The `env-template.txt` in this directory, which contains more environment 
variable definitions to configure those additional containers, should be copied to `.env` and modified before running 
docker-compose.

```bash
cp .support/env-template.txt .env
vim .env
docker-compose -f .support/docker-compose.yml up
```

When the containers show a healthy state, you can examine the PingDataGovernance configuration by logging into the    
PingData Console [https://localhost:5443/console/](https://localhost:5443/console/) with the following information:    
    
   | PingDataConsole `Server` | Username      | Password      |    
   | ------------------------ | ------------- | ------------- |    
   | pingdatagovernance       | administrator | 2FederateM0re |

You will want to update the pingdatagovernance container to use external PDP mode. This can either be done using the 
PingData Console, or using the following Docker command:

```bash
docker exec pingdatagovernance /opt/out/instance/bin/dsconfig --no-prompt set-policy-decision-service-prop \
    --set pdp-mode:external  \
    --set "policy-server:Policy Administration GUI" 
```

When bringing the containers down, don't forget to use the same `docker-compose.yml` file so that docker-compose will
bring the right set of containers down.

```bash
docker-compose -f .support/docker-compose.yml down
```

## Data generation

The smart-hub-application project expects a `hub.json` file, which contains the backing data for the smart-home. In
addition, the ExampleTokenResourceLookupMethod used in the pingdatagovernance server profile requires a JSON file
mapping usernames (or in our case, UUIDs) to arbitrary user-attribute JSON objects. This file is in
[users.json](../server-profiles/pingdatagovernance/instance/users.json). Generally, you should not 
regenerate these JSON files because the regeneration process produces random data, which will affect documented 
expected values of test scenarios.  However, there might be a need to regenerate the data.

After the randomized data is generated, known constant values are added to the data in order for the tests to produce
predictable results. The logic used to apply these values can be seen in [index.js](./generate-data/index.js).

### Usage

A convenience script has been provided that runs all components of the data generation in Docker.

```bash
.support/generate.sh
```

This should be all a maintainer should need to generate the data. If additional detail is desired, the following
subsections indicate how to run each component individually without Docker.

### generate-data

The data generation uses the [json-schema-faker](https://github.com/json-schema-faker/json-schema-faker) and the
[faker](https://www.npmjs.com/package/faker) JavaScript libraries on Node.js. The JSON schemas are
in `.support/generate-data/schemas`. You can run data generation natively with Node.js installed (v14.11.0 at time of
writing):

#### Usage

```bash
cd .support/generate-data
npm install
node index.js >hub.json
```

### extract-users

Once the data is generated, the users must be extracted for consumption by the `ExampleTokenResourceLookupMethod`. This
is done using the following jq command.

#### Usage

```bash
jq -c -M .persons smart-hub-application/hub.json >users.json
```

