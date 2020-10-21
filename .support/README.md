# PingDataGovernance Sideband Test Environment Developer Guide

## Overview

This document describes features of the pdg-sideband-test-environment relevant to developers. It does not cover the
GitLab CI pipeline, which is covered in [.gitlab-ci/](../.gitlab-ci/).

## backend-rest-application

The backend-rest-application project is a DropWizard project that simulates serving Resources for a simulated smart
device collection. For convenience, we provide a Dockerfile that compiles the project and runs it. For additional
convenience, we provide a `./run-backend.sh` bash script that builds the Docker image and runs it.

The backend-rest-application project layout follows
[DropWizard conventions](https://www.dropwizard.io/en/latest/manual/core.html#organizing-your-project). This means that
if there are any data structure changes, corresponding changes must be made in the
  `com.pingidentity.dg.backend_rest.api` Java package.

In addition to DropWizard, the project uses [lombok](https://projectlombok.org/) for developer convenience.

### Native usage

```bash
cd backend-rest-application
mvn clean package
java -jar target/backend-rest-application-<VERSION>.jar server config.yml
```

### Docker usage

```bash
./run-backend.sh
```

--or--

```bash
cd backend-rest-application
docker run "$(docker build -q .)"
```

## Data Generation

The backend-rest-application project expects a `hub.json` file, which contains the backing data for the smart-home. In
addition, the ExampleTokenResourceLookupMethod used in the pingdatagovernance server profile requires a JSON file
mapping usernames (or in our case, UUIDs) to arbitrary user-attribute JSON objects. This file is in
`server-profiles/pingdatagovernance/instance/users.json`. Generally, you should not regenerate these JSON files because the
regeneration process produces random data, which will affect documented expected values of test scenarios. However,
there might be a need to regenerate the data.

### Usage

The repository contains two ways to generate data. The first builds and uses Docker images; the second assumes 
the developer has all of the necessary dependencies (Maven, Node.js, jq, etc.) installed and runs them natively. The
native entrypoint should have better performance and reduced resource use.

```bash
.support/generate.sh
```

--or--

```bash
.support/generate-native.sh
```

### generate-data

The data generation uses the [json-schema-faker](https://github.com/json-schema-faker/json-schema-faker) and the
[faker](https://www.npmjs.com/package/faker) JavaScript libraries on Node.js. The JSON schemas are
in `.support/generate-data/schemas`. You can run data generation natively with Node.js installed:

#### Native usage

```bash
cd .support/generate-data
npm install
node index.js > hub.json
```

#### Docker usage

```bash
cd .support/generate-data
docker run "$(docker build -q .)" > hub.json
```

### extract-users

Once the data is generated, the users must be extracted for consumption by the `ExampleTokenResourceLookupMethod`. This
is done using jq. Again, a Dockerfile is provided for convenience.

#### Native usage

```bash
jq -c -M .persons backend-rest-application/hub.json > users.json
```

#### Docker usage

```bash
cd .support/extract-users
docker run "$(docker build -q .)" > users.json
```

