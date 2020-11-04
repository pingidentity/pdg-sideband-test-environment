# Simulated Sideband Adapter

This contains a NodeJS application that simulates a PingDataGovernance Sideband API consumer adapter.

## Prerequisites

* NodeJS/npm (tested with v11.10.0/6.14.8)

## Usage

Make sure [PingDataGovernance](../../docker-compose.yml) and the ['smart-hub-application'](../../smart-hub-application)
are both running and reachable.

Modify `assets/config.json` if needed. Then run the following commands from the root of the NodeJS application.

```bash
npm install
npm start
```

This will start the server on the configured port (default `8080`). You should be able to make the following request:

```bash
curl --location --request GET 'http://localhost:8080/homes' \
--header 'Authorization: Bearer { "active": true, "sub": "7d9fc465-ec1a-460c-ab38-435471f4918b" }' \
--header 'X-App-Id: SAS'
```
