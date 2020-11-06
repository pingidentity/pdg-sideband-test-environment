# PingDataGovernance sideband test environment

## Overview

This repository contains source files to help sideband adapter developers create custom PingDataGovernance
sideband adapters for their custom API Gateway environments.

## Prerequisites

* `docker` (version 19.03.13)
* `docker-compose` (version 1.27.4)
* You have followed the documentation at <https://pingidentity-devops.gitbook.io/devops/getstarted> to obtain a DevOps
  key and set up the `ping-devops` environment and command-line tool (version 0.6.8).

## Configuration

The following table lists the default ports used and their respective environment variables. To change the default
ports, copy the provided `env-template.txt` file to `.env` for use by `docker-compose` and then modify the ports.


| Environment variable                 | Default port | Description                         |
| ------------------------------------ | -----------: | ----------------------------------- |
| PDG\_SIDEBAND\_SMART\_HUB\_PORT      | 6443         | The smart-hub REST API port.        |
| PDG\_SIDEBAND\_DG\_PORT              | 7443         | The PingDataGovernance Server port. |

## Usage

A correctly implemented PingDataGovernance sideband adapter communicates with PingDataGovernance in sideband mode
and uses the responses to indicate to its API gateway whether requests should be forwarded to a
backend REST API application.

This repository allows sideband developers to deploy both PingDataGovernance and an example backend REST API application
(smart-hub-application) to different hosts/networks in order to simulate more production-like environments. The 
following sections describe how to run these components.

### PingDataGovernance sideband mode

#### Bringing up the environment

1. Copy the `env-template.txt` file to `.env` if you have not already done so.

   ```bash
   cp env-template.txt .env
   ```

2. Use `docker-compose` from the project root to bring up PingDataGovernance in sideband mode. This also brings up the
   PingData Console.

   ```bash
   docker-compose up --detach
   ```

3. Wait a few moments for the started containers to become healthy. Check their
   state by running the following command:

   ```bash
   docker container ls --format '{{ .Names }}: {{ .Status }}'
   ```

#### Bringing down the environment

1. To stop and remove the containers, run the following command. Note that removing the containers clears any changes
you made to the PingDataGovernance configuration.

   ```bash
   docker-compose down
   ```

### smart-hub-application

#### Bringing up the environment

1. Run the following command:

   ```bash
   ./run-smart-hub.sh
   ```

2. Wait for the server to start. This might take some time (up to 5 minutes on some machines) because the script builds the
   Docker image, compiles the application, and runs it. If the server has started successfully, you should see
   text similar to the following line in the console:

   ```
   INFO  [2020-10-07 17:52:11,066] org.eclipse.jetty.server.Server: Started @2109ms
   ``` 

3. Verify that the server is running by using an HTTP client like `curl` to access an endpoint.

   ```bash
   curl --header "SH-USER: d1f988e8-ae56-485e-a905-dfd478252b7b" http://localhost:8443/homes
   ```

   You should receive a JSON array of data.

### Developing the PingDataGovernance sideband adapter

#### Shared secret

To access the PingDataGovernance Server in sideband mode, a sideband adapter must provide a configurable shared
secret through an HTTP header (`PDG-TOKEN`). The sideband test environment is pre-configured with three of them. One is enabled
at start.

| Shared secret name | Shared secret                             | Enabled? |
| ------------------ | ----------------------------------------- | -------- |
| sideband-secret-1  | 5e96eaf6-251e-4555-9434-d1b224f05e99      | Yes      |
| sideband-secret-2  | 2712bdb5-1ff8-4f30-8f69-dbe74a9cf4bb      | No       |
| sideband-secret-3  | 2c88b3fa-c43c-4e8b-8716-6ea3c051a6f7      | No       |


To rotate the shared secret in the `docker-compose` environment, use the following command:

```bash
docker exec pingdatagovernance /opt/out/instance/bin/dsconfig --no-prompt set-http-servlet-extension-prop \
  --extension-name "Sideband API" \
  --set shared-secrets:sideband-secret-2
```

This command disables sideband-secret-1 and enables sideband-secret-2.

