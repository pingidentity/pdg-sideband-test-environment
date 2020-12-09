# PingDataGovernance sideband test environment

This repository contains source files to help sideband adapter developers create custom PingDataGovernance
sideband adapters for their custom API gateway environments.

A correctly implemented PingDataGovernance sideband adapter communicates with PingDataGovernance in sideband mode
and uses the responses to indicate to its API gateway what (if anything) it should forward to the
backend REST API application.

The below diagram breaks down a single client HTTP request lifecycle into subrequests from the perspective of the API
gateway/sideband adapter pair.

## Sideband message map
![Sideband message map](./assets/sideband-message-map.svg)

The above diagram will be referenced throughout this document.

This repository provides the following:

* A [PingDataGovernance Server](#pingdatagovernance-sideband-api) fully-configured for Sideband API mode
* An example backend [REST API application](#smart-hub) with production-like data simulating a smart device API (smart-hub)
* A user data store, shared by the smart-hub application and the PingDataGovernance Server
* Pre-configured PingDataGovernance policies written for the user data store and the smart device API
* A [Test harness](#test-harness) which uses a Postman collection for automated testing of a sideband adapter implementation

## Prerequisites

* `docker` (version 19.03.13)
* `docker-compose` (version 1.27.4)
* You have followed the documentation at <https://pingidentity-devops.gitbook.io/devops/getstarted> to obtain a DevOps
  key and set up the `ping-devops` environment and command-line tool (version 0.6.8).

> :warning: **When deploying individual components to different hosts**, each host must meet the prerequisites.

## Suggested developer workflow

A sideband adapter developer should:

1. Read the [PingDataGovernance Sideband Integration Guide](https://docs.google.com/document/d/1hb3u6sfm8LWM9B_MqSD0ncOTSb0NgTW-tKWG1OLCOVA/edit?usp=sharing)
   and review the [sideband message map](#sideband-message-map).

2. Understand how the different components work together and communicate.

3. [Deploy the repository components](#running-the-components) in a simulation environment.

4. Ensure that their API gateway/sideband adapter can communicate with the environment's components.
   See the [Configuration](#configuration) section for more details.

5. Begin developing the sideband adapter to implement the sideband message map. This involves sending HTTP subrequests
   to the PingDataGovernance Sideband API. Example requests/responses are found in the integration guide.
   See also the [live example request/responses](#sideband-api-request-examples) specifically for the smart-hub
   application and test policies.

6. Verify the sideband adapter can handle requests and is ready for testing.

7. Configure the test harness to point to their API gateway/sideband adapter environment, and run the tests. 

8. Troubleshoot failures by understanding how the test should work, then [examining logs](#viewing-logs-and-troubleshooting)
   from the components to identify where the test failed.

A successful test run indicates that the sideband adapter is ready for testing against more production-like APIs and
policies in the organization's test environment.

## Running the components

This repository allows sideband adapter developers to deploy both PingDataGovernance and the smart-hub REST API application
to different hosts/networks to simulate more production-like environments. The following sections describe how to run
these components.

> :warning: All of the provided shell commands/examples should be run from the project root.

### Configuration

The following table lists the environment variables used by the different sideband test environment components.
To change the default values, copy the provided `env-template.txt` file to `.env` and modify the values.


| Environment variable                    | Default value          | Description                                                                                                         |
| --------------------------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------- |
| PDG\_SIDEBAND\_SMART\_HUB\_PORT         | 6443                   | The smart-hub REST API port.                                                                                        |
| PDG\_SIDEBAND\_DG\_PORT                 | 7443                   | The PingDataGovernance Server port.                                                                                 |
| PDG\_SIDEBAND\_DG\_HOST                 | localhost              | The PingDataGovernance Server host for displaying [live example request/responses](#sideband-api-request-examples). |
| PDG\_SIDEBAND\_TEST\_HARNESS\_BASE\_URL | https://localhost:9443 | The base URL that the test harness should use when executing tests.                                                 |
| PDG\_SIDEBAND\_REQUEST\_CONTEXT\_METHOD | state                  | The [request-context-method](#request-context-method) value supported by the API gateway.                           |

> :warning: **When deploying components to different hosts**, be sure that each host has a cloned repository, and that
> each has a correct `.env` file, consistent with the other hosts.

### PingDataGovernance Sideband API

The docker-compose environment provided in this repository configures a PingDataGovernance Server in Sideband API mode.
This section describes how to bring up the environment after having cloned the repository to the desired host.

#### Bringing up the environment

1. Copy the `env-template.txt` file to `.env` if you have not already done so. Edit the file if necessary.

   ```bash
   cp env-template.txt .env
   vi .env
   ```

2. Use `docker-compose` from the project root to bring up the PingDataGovernance Sideband API.

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

### smart-hub

The smart-hub application is the backend REST API assumed by this repository's test harness. This section
describes how to start the server after having cloned the repository to the desired host.

#### Bringing up the environment

1. Copy the `env-template.txt` file to `.env` if you have not already done so. Edit the file if necessary.

   ```bash
   cp env-template.txt .env
   vi .env
   ```

2. Run the following command:

   ```bash
   ./run-smart-hub.sh
   ```

3. Wait for the server to start. This might take some time (up to 5 minutes on some machines) because the script builds the
   Docker image and runs it. If the server has started successfully, you should see text similar to the following line in
   the console:

   ```
   INFO  [2020-10-07 17:52:11,066] org.eclipse.jetty.server.Server: Started @2109ms
   ``` 

4. Verify that the server is running by using an HTTP client like `curl` to access an endpoint.

   ```bash
   curl --header "SH-USER: 7d9fc465-ec1a-460c-ab38-435471f4918b" http://localhost:6443/homes
   ```

   You should receive a JSON array of data and see logs generated in the running console.

#### Bringing down the environment

1. Use `CTRL+c` in the running console to stop the server.

### Postman collections

This repository contains two Postman collections. Both collections simulate messages in the
[sideband message map](#sideband-message-map), but originating from different components. Execute the collections using
the `run-test-harness.sh` script. Details and instructions are provided in the subsections below.

#### Sideband API request examples

The
[sideband-api-request-examples.postman_collection.json](./test-harness/sideband-api-request-examples.postman_collection.json)
simulates the requests that a sideband adapter would make against the PingDataGovernance Sideband API. This serves two
purposes:

* It provides sample request/responses to a sideband adapter developer.
* It ensures that the test policies work as expected.

To run this collection, complete these steps:

1. Ensure the PingDataGovernance Sideband API is running.

2. Copy the `env-template.txt` file to `.env`, and modify the values if you have not already done so.

   ```bash
   cp env-template.txt .env
   vi .env
   ```

3. Run the following command:

   ```bash
   ./run-test-harness.sh sideband-api-request-examples
   ```

   You should see the example request/responses in the resulting console output (example below).
   
   ```
   → 1 - Retrieve homes
     POST https://localhost:7443/sideband/request [200 OK, 1.2KB, 519ms]
     ┌
     │ 'Request Headers:'
     │ '  Accept: application/json'
     │ '  PDG-TOKEN: 5e96eaf6-251e-4555-9434-d1b224f05e99'
     │ '  Authorization: Bearer {\\"active\\":true,\\"sub\\":\\"7d9
     │ fc465-ec1a-460c-ab38-435471f4918b\\"}'
     │ '  Content-Type: application/json'
     │ '  User-Agent: PostmanRuntime/7.26.5'
     │ '  Cache-Control: no-cache'
     │ '  Postman-Token: a437aba2-6735-4e46-a6fb-3b0fa0d4e59b'
     │ '  Host: localhost:7443'
     │ '  Accept-Encoding: gzip, deflate, br'
     │ '  Connection: keep-alive'
     │ '  Content-Length: 351'
     │ 'Request Body:'
     │ '  {"source_ip":"142.135.86.129","source_port":743,"method":
     │ "GET","url":"http://localhost:8080/homes","http_version":"1.1","h
     │ eaders":[{"Authorization":"Bearer {\\"active\\":true,\\"sub\\":\\
     │ "7d9fc465-ec1a-460c-ab38-435471f4918b\\"}"},{"X-App-Id":"PNG"}]}'
     │ 
     │ 'Response Headers:'
     │ '  Date: Mon, 07 Dec 2020 19:35:37 GMT'
     │ '  Correlation-Id: e94be6f1-24cc-4e68-955e-8f3cc2741094'
     │ m
     │ '  Content-Type: application/json'
     │ '  Content-Length: 1067'
     └
   ```

#### Test harness

After setting up the sideband adapter to handle HTTP client requests, a developer can use a
Postman collection and test harness provided in this repository to validate their implementation. To do so, the
developer must first configure their API gateway to proxy an instance of the [smart-hub application](#smart-hub). 
Please consult the API gateway documentation for how to do this, as instructions are specific to each API gateway.
The sideband adapter should use the `/sideband/request` and `/sideband/response` endpoints from the 
[PingDataGovernance Sideband API](#pingdatagovernance-sideband-api) in this repository.

To run the test harness, complete these steps:

1. Copy the `env-template.txt` file to `.env`, and modify the values if you have not already done so.

   ```bash
   cp env-template.txt .env
   vi .env
   ```

2. Run the following command:

   ```bash
   ./run-test-harness.sh
   ```

   You should see the test harness make the requests in the collection and output test results, highlighting test
   failures. An example request is shown below:

   ```
   → 1 - Retrieve homes
     ┌
     │ 'The test policies `DENY` client reque
     │ sts that are missing an `X-App-Id` header. 
     │ This test makes a simple `GET` request with
     │  that header to make sure the sideband adap
     │ ter correctly transforms client request hea
     │ ders when fetching inbound decisions from P
     │ ingDataGovernance.'
     └
     GET http://localhost:8080/homes [200 OK, 4.8KB, 304ms]
     ✓  Response is valid JSON
     ✓  Status code is 200
   ```

   Each request indicates its smart-hub API call, a description of the relevant policies and sideband adapter aspects,
   and the individual test expectations along with their pass/fail status.

## Viewing logs and troubleshooting

View the HTTP request/responses 2, 3, 6, and 7 from the
[PingDataGovernance Server debug-trace logs](#pingdatagovernance-server-debug-trace-logs).
View request/responses 4 and 5 from the [smart-hub application console](#smart-hub-logs).
View the client request/response 1 and 8 either on the client side, or on the API gateway/sideband
adapter side. Consult the documentation for the specific client or API gateway for instructions.

### PingDataGovernance Server debug-trace logs

The PingDataGovernance Server configured in this repository has multiple log sinks that you can examine while
troubleshooting. Use the Debug Trace Logger to view the full HTTP requests and responses handled by the 
PingDataGovernance Sideband API. The following command tails the debug-trace logs within the pingdatagovernance Docker
container:

```bash
docker exec -it pingdatagovernance tail -f /opt/out/instance/logs/debug-trace
```

When the sideband adapter makes subrequests 2 and 6, 
those HTTP requests and the responses 3 and 7 are logged here.  The following is an example log entry
for a subrequest 2 to `/sideband/request`.

```
[02/Dec/2020:19:46:51.472 +0000] DEBUG HTTP-FULL-REQUEST-AND-RESPONSE requestID=212 correlationID="257c628c-d7bc-4f0e-b9d1-5669c22d96aa" accessTokenId="" product="Ping Identity Data Governance Server" instanceName="518b86fcb223" startupID="X8fZDA==" threadID=122 from=172.23.0.1:57404 method=POST url="https://172.23.0.2:443/sideband/request" statusCode=200 etime=222.647 responseContentLength=1573 msg="
 Request Headers:
  PDG-TOKEN: 5e96eaf6-251e-4555-9434-d1b224f05e99
  Accept: application/json
  User-Agent: node-fetch/1.0 (+https://github.com/bitinn/node-fetch)
  Connection: close
  Host: localhost:7443
  Accept-Encoding: gzip,deflate
  Content-Length: 524
  Content-Type: application/json
 Request Cookies:
 Request Parameters:
 Request Body:
  {'source_ip':'::1','source_port':46248,'url':'http://localhost:6443/persons/7d9fc465-ec1a-460c-ab38-435471f4918b','method':'GET','http_version':'1.1','headers':[{'X-App-Id':'PNG'},{'Accept':'application/json'},{'Authorization':'Bearer {\'active\':true,\'sub\':\'705fd0eb-380c-4007-a2da-900ea8d58987\'}'},{'User-Agent':'PostmanRuntime/7.26.5'},{'Cache-Control':'no-cache'},{'Postman-Token':'3db157db-9313-4f00-8231-5e43de891ed7'},{'Host':'localhost:8080'},{'Accept-Encoding':'gzip, deflate, br'},{'Connection':'keep-alive'}]}
 Response Headers:
  Connection: close
  Date: Wed, 02 Dec 2020 19:46:51 GMT
  Correlation-Id: 257c628c-d7bc-4f0e-b9d1-5669c22d96aa
  Content-Length: 1573
  Content-Type: application/json
 Response Cookies:
 Response Body:
  {'source_ip':'::1','source_port':46248,'method':'GET','url':'http://localhost:6443/persons/7d9fc465-ec1a-460c-ab38-435471f4918b','http_version':'1.1','headers':[{'Authorization':'Bearer {\'active\':true,\'sub\':\'705fd0eb-380c-4007-a2da-900ea8d58987\'}'},{'Accept':'application/json'},{'Cache-Control':'no-cache'},{'SH-USER':'705fd0eb-380c-4007-a2da-900ea8d58987'},{'User-Agent':'PostmanRuntime/7.26.5'},{'Connection':'keep-alive'},{'Postman-Token':'3db157db-9313-4f00-8231-5e43de891ed7'},{'Host':'localhost:8080'},{'Accept-Encoding':'gzip, deflate, br'}],'state':'H4sIAAAAAAAAAK1UuXLbMBD9FQ5qguIBikcVW-OjsBNPbKeJMhoQWImIKYABQV8a_XsWlBy3KlKR2OvtLt7Djlj4M8LgSL0jgxmtgJXqSU3qOiHhh6U3FgPYPGVlSLbgWiMx5OriAUNG2-F_61xfz2adEbxrzeDqOWPZrAc7GD3MClmtBZvnFETCKZvHgvImKynLclYka1YlZYOlfJHVM-Yoo7FmEvkWWuASTaT-uSNnI0Jb9c7dIeIcuAUb7JaEC6eeYUlqZ0cIl2QYGzwsSRHnaxlDQ7MSQVkcF5SnktMqjoGXMi-rsliSPdmHWF0I6HFOwvu-U2ICmf3GASbvgosW6MJoZ42fWBsqvGly3l_Tx_uL72g-BXBKeRzA0rMNaI94hyvbcv191E5tYVZE6TzKD7BGa8Dhpo08AfSUdzjp5Dtm0QfzBH5hmWySvJANrbIko2wdx7RMs4TmwDIJZZWALKbEa4TD-M_bKuMynjyHHdALLYxUeoNBm3fVh4GEdccdhEFjyf7XPiTOY_7AXiR3xmLcrRFPgU8fhmBqKPj0hng_3nFsdHc8rqYimPsfLtBDeAocGUBGXO9H_YkTnhIIddL9HMdbmRcNfraTkj6W8u2QtCOab7Gd05JD1OFBfl6Ix8xbsB0El7j4Dt681HCmo6vLMrdq-SuuPiSw5cpTcmHfBse76HwUT1bJDXzZeE8kzBajuJSIgUJCoTsL4BlwpbjWKrj0-ka1j8r5ju_9N4jnKdqEcm9o-ooRbbBoOXakAe3ICqSIj07TeR7TnGUMzRswHgBbRg8tWVSkRY72buJSVUU5YyXZ46761mAhVDnNqjllaU7zJI-D1yyLY0x4gWY4dDM4eOHWRY16RzvO0nONLf3b0jXvno1_Z4Kbm4WP4E60d63lgy-PT4bZInNlsFWDf1iosMqhurtAtLzDaTZ-nAb3glfQj7Y3AwRrqybNdZ7N61FPByS7UzCgTojy799prNjv_wLXxQblZAUAAA'}"
```

These and similar log entries for `/sideband/response` can indicate issues with the sideband adapter's ability to
generate subrequests for the PingDataGovernance Sideband API.

### smart-hub logs

The smart-hub application, similar to PingDataGovernance, logs full requests and responses to the console. An example
request/response for a PATCH to a control is displayed below.

```
172.17.0.1 - - [02/Dec/2020:18:14:21 +0000] "PATCH /controls/1da1a20f-6707-40b3-a2af-a6bc9054b640 HTTP/1.1" 200 125 "-" "PostmanRuntime/7.26.5" 24
INFO  [2020-12-02 19:45:53,941] org.glassfish.jersey.logging.LoggingFeature: 7 * Server has received a request on thread dw-22 - GET /homes
7 > GET http://localhost:8080/homes
7 > Accept: application/json
7 > Accept-Encoding: gzip, deflate, br
7 > Authorization: Bearer {"active":true,"sub":"7d9fc465-ec1a-460c-ab38-435471f4918b"}
7 > Cache-Control: no-cache
7 > Connection: keep-alive
7 > Host: localhost:8080
7 > Postman-Token: 463afb97-dfa2-4c91-9d1c-57d3ad7e3cbd
7 > SH-USER: 7d9fc465-ec1a-460c-ab38-435471f4918b
7 > User-Agent: PostmanRuntime/7.26.5

INFO  [2020-12-02 19:45:53,946] org.glassfish.jersey.logging.LoggingFeature: 7 * Server responded with a response on thread dw-22 - GET /homes
7 < 200
7 < Content-Type: application/json
7 < X-Banned-Users: l33t_haxor
7 < X-Powered-By: SmartHub Server Supreme
[{"id":"5b99ccf0-a164-4472-baae-6243768f0a5b","name":"infomediaries","image":"http://placeimg.com/32/32/business?19253","geo":{"lat":"43.9169","lng":"-96.9792"},"members":[{"id":"7d9fc465-ec1a-460c-ab38-435471f4918b","name":"Geoffrey Jacobi","username":"Estefania_Johnston93","email":"Brigitte.Larson96@gmail.com","phone":"1-678-789-5398 x76495","website":"jon.info","address":{"street":"Ondricka Mills","suite":"Apt. 878","city":"McCulloughberg","zipcode":"08597-8731","geo":{"lat":"85.7690","lng":"-72.3942"}},"company":{"name":"Gerhold Group","catchPhrase":"Multi-tiered grid-enabled success","bs":"orchestrate holistic bandwidth"}},{"id":"f36d5a4d-928b-4c2e-b756-5dcf4a04d2d6","name":"Ms. Elias Parisian","username":"Caleigh_Runolfsdottir","email":"Peter1@hotmail.com","phone":"828-450-5544","website":"americo.biz","address":{"street":"King Field","suite":"Apt. 651","city":"Paxtontown","zipcode":"68214-4942","geo":{"lat":"84.8683","lng":"-43.3431"}},"company":{"name":"Rodriguez - Rippin","catchPhrase":"Switchable radical toolset","bs":"harness intuitive technologies"}}],"devices":[{"id":"7c9149b9-1892-4f30-bc3d-50fdac5c87c2","name":"Innovative model","type":"appliance","product":"Licensed Plastic Soap","vendor":"Trantow, Runolfsson and Runte","controls":[{"id":"0096b360-ef51-473a-ac48-14369c081182","name":"media_content","value":62.36294631338606,"status":"draft","callsRemaining":3,"readOnly":true},{"id":"a2895516-104e-493d-bf1c-651f069e4b7e","name":"level","value":74.47962622850726,"status":"draft","callsRemaining":10,"readOnly":true}]},{"id":"a762a83c-78bd-4b4b-8775-0eb542218067","name":"Berkshire EXE","type":"dial","product":"Generic Steel Cheese","vendor":"Mante Group","controls":[{"id":"9ae28efb-852b-4144-8d68-9ea222df76bb","name":"temperature","value":26.955237849027334,"status":"completed","callsRemaining":9,"readOnly":true}]},{"id":"50c6f4d7-2311-4a26-b4ea-284cd8096aef","name":"Account Wyoming","type":"scale","product":"Ergonomic Soft Bacon","vendor":"Dicki, Upton and Bechtelar","controls":[{"id":"31be6f94-f228-4bef-b467-d5ebd690dcab","name":"speed","value":80.06295287090883,"status":"connected","callsRemaining":4,"readOnly":true},{"id":"75a4c1e9-91a4-444e-97f9-e2aade00cb5e","name":"carbon_monoxide","value":26.110541988646595,"status":"off","callsRemaining":10,"readOnly":true},{"id":"f11de1f8-3125-4181-a210-8228a4b62c3d","name":"speed","value":57.934849308564715,"status":"online","callsRemaining":9,"readOnly":false}]},{"id":"2c7bb479-0937-4032-91fa-fad4557a1ac3","name":"RAM Gambia","type":"appliance","product":"Ergonomic Metal Cheese","vendor":"Kemmer - Gusikowski","controls":[{"id":"159fe25b-4bbe-4746-a799-2735a5379459","name":"speed","value":87.24443987226189,"status":"draft","callsRemaining":2,"readOnly":true},{"id":"041aa498-b9bc-407f-bb4a-464d2c138edc","name":"connection_status","value":99.99329982653875,"status":"offline","callsRemaining":-1,"readOnly":false},{"id":"0851a808-28f7-4add-acf7-6b3bbe730fc6","name":"level","status":"processing","callsRemaining":7,"readOnly":true},{"id":"52723fd9-4602-4f58-b599-6314bf01ec0c","name":"media_content","value":7.314771302989964,"status":"playing","callsRemaining":8,"readOnly":true}]},{"id":"a27e91a8-8862-4ced-a697-7147614ea664","name":"Steel definition","type":"detector","product":"Sleek Plastic Pants","vendor":"Sawayn - Murphy","controls":[{"id":"b86ba652-2eb4-48a1-bea9-9cdc3d2a1dab","name":"channel","value":13.241030956026979,"status":"recording","callsRemaining":1,"readOnly":false},{"id":"9a8fedc6-7c84-4043-992a-ffb07aea2cba","name":"level","value":47.01707368826305,"status":"pending","callsRemaining":2,"readOnly":false},{"id":"85365562-8beb-4c15-a08a-a07cdf0e22cf","name":"media_content","value":77.83044460108222,"status":"unknown","callsRemaining":7,"readOnly":true},{"id":"a645530f-2f90-437a-95fe-472e35722287","name":"wind_speed","status":"false","callsRemaining":9,"readOnly":true},{"id":"3ddba559-74a7-4f31-a605-da79a874b6ca","name":"volume","value":92.2636739955373,"status":"processing","callsRemaining":4,"readOnly":false}]},{"id":"83f6e6ae-96fe-4ba9-9480-88a3a33a7add","name":"Josh's luxury sedan","type":"vehicle","product":"Carnivale","vendor":"Panaphonics","controls":[{"id":"1da1a20f-6707-40b3-a2af-a6bc9054b640","name":"power","value":88000,"status":"off","callsRemaining":0,"readOnly":false}]},{"id":"4aeb0a59-f731-4e12-be5b-1ff420b6eef3","name":"e-enable Dynamic","type":"media_player","product":"Refined Rubber Shirt","vendor":"Heaney - Kozey","controls":[{"id":"2508de80-5d46-4cc6-8690-a4ad6e9f047f","name":"connection_status","value":91.45720621366439,"status":"online","callsRemaining":3,"readOnly":false}]},{"id":"83f6e6ae-96fe-4ba9-9480-88a3a33a7add","name":"Josh's luxury sedan","type":"vehicle","product":"Carnivale","vendor":"Panaphonics","controls":[{"id":"1da1a20f-6707-40b3-a2af-a6bc9054b640","name":"power","value":88000,"status":"off","callsRemaining":0,"readOnly":false}]}]}]
```

## Appendix

### Shared secret

To access the PingDataGovernance Server in sideband mode, a sideband adapter must provide a configurable shared
secret through an HTTP header (`PDG-TOKEN`). The sideband test environment is pre-configured with three of them. One is enabled
at start.

| Shared secret name | Shared secret                             | Enabled? |
| ------------------ | ----------------------------------------- | -------- |
| sideband-secret-1  | 5e96eaf6-251e-4555-9434-d1b224f05e99      | Yes      |
| sideband-secret-2  | 2712bdb5-1ff8-4f30-8f69-dbe74a9cf4bb      | No       |
| sideband-secret-3  | 2c88b3fa-c43c-4e8b-8716-6ea3c051a6f7      | No       |


To rotate the shared secret in the `docker-compose` environment, use a command line the one below, which disables
sideband-secret-1 and enables sideband-secret-2:

```bash
docker exec pingdatagovernance /opt/out/instance/bin/dsconfig --no-prompt set-http-servlet-extension-prop \
  --extension-name "Sideband API" \
  --set shared-secrets:sideband-secret-2
```

### Request context method

In messages 6 and 7, the sideband adapter retrieves the outbound policy decision from the
PingDataGovernance Sideband API. Depending on an organization's data governance business needs, an outbound policy 
might require data generated earlier in the client request lifecycle. However, some API gateways are not 
capable of maintaining data across subrequests. To handle different API gateway limitations, the PingDataGovernance 
Sideband API has a configuration attribute called `request-context-method`, which can have one of three different
values, each of which is outlined in the table below. The pre-configured value in `env-template.txt` is `state`.

| request-context-method | API gateway requirements                                                              | Performance implication                | Outbound policy writer limitations |
| ---------------------- | ------------------------------------------------------------------------------------- | -------------------------------------- | ---------------------------------- |
| `state`                | Supports populating subrequest `6` with the `state` JSON attribute retrieved from `3` | Fastest way to provide request context | None                               |
| `request`              | Supports populating subrequest `6` with the same request data sent in `2`             | Requires redundant processing          | None                               |
| `none`                 | Does not support `state` or `request`                                                 | No additional processing               | Request data is unavailable        |

While the `request-context-method` value could be dictated by the API gateway limitations, an organization could alternatively
decide in advance that outbound policies will never need to reference request context data and choose `none` to reduce
sideband adapter implementation time. Regardless, in the scenario where `none` is selected, policy writers should be
notified that outbound policies will not have that data available.

After you decide on a `request-context-method`, a sideband adapter developer can control the value configured in the
sideband test environment by modifying their `.env` file in the root of this repository. The value
`PDG_SIDEBAND_REQUEST_CONTEXT_METHOD` is consumed by the PingDataGovernance Server docker-compose environment and the
test harness, which skips related tests if the value is set to `none`.

### Postman collection coverage

The Postman collection [sideband-adapter.postman_collection.json](./test-harness/sideband-adapter.postman_collection.json)
has a number of requests and test cases associated with the requests. The test cases construct/submit HTTP request 1 and
inspect HTTP request 8 for expected values, which indicate that the system is working correctly.

### Common pitfalls

* Make sure that your sideband adapter is configurable on a per-environment basis. At a minimum, this means being able to
  configure the following items:

  * The PingDataGovernance Sideband API base URL
  * The PingDataGovernance Sideband API shared secret (for example the `PDG-TOKEN` request header value)
  * The backend REST API base URL

* The API gateway/sideband adapter should remove or overwrite response headers appropriately. Some response headers,
  like `Transfer-Encoding` or `Content-Encoding`, while returned by the backend REST API, are intended for the direct
  message recipient and should not be forwarded to the client. For example, the backend REST API might be capable of a
  `Transfer-Encoding` value of `chunked`. However, if the API gateway proceeds to forward this response header but does
  not chunk its response, the client might fail to parse the response. Similarly, if the backend REST API responds with a
  `Content-Encoding` of `gzip`, the API gateway should not forward this response header if it is not gzipping the
  response.
