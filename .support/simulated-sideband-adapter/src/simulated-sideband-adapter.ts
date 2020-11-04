import fetch, { Headers as SubHeaders, RequestInit as SubRequestConfig, HeadersInit as SubRequestHeaders, Response as SubResponse } from 'node-fetch'
import express, { Express, Request as ClientRequest, Response as ClientResponse } from "express";
import {
    Headers as SidebandHeaders,
    HttpResponse as SidebandHttpResponse,
    InboundResponse as InboundSidebandResponse,
    InboundRequest as InboundSidebandRequest,
    OutboundRequest as OutboundSidebandRequest,
    OutboundResponse as OutboundSidebandResponse
} from "./pdg-sideband-api";

/**
 * Configuration for the simulated sideband adapter.
 */
export interface SimulatedSidebandAdapterConfig {
    pdgBaseUrl: string;
    pdgSharedSecretHeaderName: string;
    pdgSharedSecretHeaderValue: string;
    gatewayPort: number;
    backendBaseUrl: string;
    requestContextMethod: 'state' | 'request' | 'none';
}

/**
 * A class that simulates a PingDataGovernance sideband adapter.
 */
export class SimulatedSidebandAdapter {

    private server: Express;

    /**
     * Forwards an HTTP response from the PingDataGovernance Sideband API to the client.
     *
     * @param sidebandHttpResponse The PingDataGovernance sideband http response.
     * @param clientResponse  The client response.
     */
    private forwardSidebandHttpResponse(sidebandHttpResponse: SidebandHttpResponse, clientResponse: ClientResponse): void {
        if (sidebandHttpResponse.headers && sidebandHttpResponse.response_code) {
            this.log.info('Sending HTTP sideband response...');
            this.log.debug(sidebandHttpResponse);
            this.applySidebandHeaders(sidebandHttpResponse.headers, clientResponse);
            clientResponse.status(parseInt(sidebandHttpResponse.response_code, 10)).send(sidebandHttpResponse.body);
        }
    }

    /**
     * Constructs an inbound sideband request from a client request.
     *
     * @param clientRequest  The client request.
     */
    private buildInboundSidebandRequest(clientRequest: ClientRequest): InboundSidebandRequest {
        return {
            source_ip: clientRequest.socket.remoteAddress,
            source_port: clientRequest.socket.remotePort,
            url: `${this.cfg.backendBaseUrl}${clientRequest.originalUrl}`,
            method: clientRequest.method,
            http_version: "1.1",
            headers: this.fromClientHeaders(clientRequest.rawHeaders),
            ...(['POST', 'PUT', 'PATCH'].indexOf(clientRequest.method.toUpperCase()) !== -1) && { body: JSON.stringify(clientRequest.body) }
        };
    }

    /**
     * Processes an inbound sub response from PingDataGovernance.
     *
     * @param inboundSubResponse The inbound sub response.
     * @param clientResponse  The client response.
     * 
     * @returns The inbound sideband response or `null` if further processing should halt.
     */
    private async processInboundSubResponse(inboundSubResponse: SubResponse,
        clientResponse: ClientResponse): Promise<InboundSidebandResponse> {

        if (inboundSubResponse) {
            this.log.info('Processing inboundSubResponse...');

            if (inboundSubResponse.status !== 200) {
                this.log.error('An HTTP error occurred for the Sideband inbound request.',
                    await inboundSubResponse.text());

                // If something went wrong in the HTTP request, respond to the client with an error
                clientResponse.status(500).send('SIDEBAND_INBOUND_HTTP_ERROR');
            } else {
                const inboundSidebandResponse = await inboundSubResponse.json() as InboundSidebandResponse;
                this.log.debug('inboundSidebandResponse', inboundSidebandResponse);

                if (!inboundSidebandResponse.response) {

                    // No http response indicates we should continue processing this request
                    return inboundSidebandResponse;
                }

                // An inbound http response indicates a short-circuit
                this.log.info('Short-circuiting due to existence of inbound http response...');
                this.forwardSidebandHttpResponse(inboundSidebandResponse.response, clientResponse);
            }
        }
        return null;

    }

    /**
     * Processes a backend sub response.
     *
     * @param backendSubResponse The backend sub response.
     * @param inboundSidebandRequest The inbound sideband request.
     * @param inboundSidebandResponse The inbound sideband response.
     * @param _ The client response.
     *
     * @returns The outbound sideband request, or `null` if further processing should halt.
     */
    private async processBackendSubResponse(backendSubResponse: SubResponse,
        inboundSidebandRequest: InboundSidebandRequest,
        inboundSidebandResponse: InboundSidebandResponse,

        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        _: ClientResponse): Promise<OutboundSidebandRequest> {
        if (backendSubResponse && inboundSidebandResponse) {

            this.log.info('Processing backend sub response...');

            const backendSubResponseBody = await backendSubResponse.text();

            this.log.debug("backendSubResponseBody", backendSubResponseBody);

            const outboundSidebandRequest: OutboundSidebandRequest = {
                headers: this.fromSubResponseHeaders(backendSubResponse.headers),
                http_version: inboundSidebandResponse.http_version,
                method: inboundSidebandResponse.method,
                url: inboundSidebandResponse.url,
                response_code: `${backendSubResponse.status}`,
                response_status: backendSubResponse.statusText,
                ...(backendSubResponseBody && { body: backendSubResponseBody }),
                ...((this.cfg.requestContextMethod === 'state' && inboundSidebandResponse.state) && { state: inboundSidebandResponse.state }),
                ...((this.cfg.requestContextMethod === 'request') && { request: inboundSidebandRequest })
            };

            this.log.debug('outboundSidebandRequest', outboundSidebandRequest);

            return outboundSidebandRequest;
        }
        return null;
    }

    /**
     * Processes the outbound sub response.
     *
     * @param outboundSubResponse The outbound sub response.
     * @param clientResponse The client response.
     */
    private async processOutboundSubResponse(outboundSubResponse: SubResponse,
        clientResponse: ClientResponse): Promise<void> {
        if (outboundSubResponse) {
            this.log.info('Processing oubound sub response...');
            if (outboundSubResponse.status !== 200) {
                this.log.error('An HTTP error occurred for the Sideband outbound request.');
                this.log.error(await outboundSubResponse.text());
                clientResponse.send(500).send('SIDEBAND_OUTBOUND_HTTP_ERROR');
            } else {
                const outboundSidebandResponse = await outboundSubResponse.json() as OutboundSidebandResponse;
                this.log.debug('outboundSidebandResponse', outboundSidebandResponse);
                this.forwardSidebandHttpResponse(outboundSidebandResponse, clientResponse);
            }
        }
    }

    /**
     * Sends a PingDataGovernance sideband request.
     *
     * @param type The request type.
     * @param message The message to send.
     * 
     * @returns The fetch response from PingDataGovernance.
     */
    private async callSidebandApi(type: 'request' | 'response',
        message: InboundSidebandRequest | OutboundSidebandRequest): Promise<SubResponse> {
        if (message) {
            const fetchConfig: SubRequestConfig = {
                method: 'POST',
                headers: {
                    [this.cfg.pdgSharedSecretHeaderName]: this.cfg.pdgSharedSecretHeaderValue,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                redirect: 'follow',
                body: JSON.stringify(message)
            };
            this.log.info(`Sending ${type === 'request' ? 'inbound' : 'outbound'} request to PingDataGovernance...`);
            this.log.debug('fetchConfig', fetchConfig);
            return fetch(`${this.cfg.pdgBaseUrl}/sideband/${type}`, fetchConfig);
        }
    }

    /**
     * Sends a backend sub request.
     *
     * @param inboundResponse The inbound response.
     * 
     * @returns The sub response from the backend, or `null` if further processing should be halted.
     */
    private async performBackendSubRequest(inboundResponse: InboundSidebandResponse): Promise<SubResponse> {
        if (inboundResponse) {
            const fetchConfig: SubRequestConfig = {
                method: inboundResponse.method,
                headers: this.asSubRequestHeaders(inboundResponse.headers),
                redirect: 'follow',
                ...(inboundResponse.body && { body: inboundResponse.body })
            };
            this.log.info(`Sending backend sub request to ${inboundResponse.url}...`);
            this.log.debug('fetchConfig', fetchConfig);
            return fetch(`${inboundResponse.url}`, fetchConfig);
        }
    }

    /**
     * Handles a client request.
     *
     * @param clientRequest The client request.
     * @param clientResponse The client response.
     */
    private handleRequest(clientRequest: ClientRequest,
        clientResponse: ClientResponse): void {
        this.log.info('Handling request...');
        const inboundSidebandRequest = this.buildInboundSidebandRequest(clientRequest);
        this.callSidebandApi('request', inboundSidebandRequest)
            .then(inboundSubResponse => this.processInboundSubResponse(inboundSubResponse, clientResponse))
            .then(inboundSidebandResponse => this.performBackendSubRequest(inboundSidebandResponse).then(backendSubResponse =>
                this.processBackendSubResponse(backendSubResponse, inboundSidebandRequest, inboundSidebandResponse, clientResponse)))
            .then(outboundSidebandRequest => this.callSidebandApi('response', outboundSidebandRequest))
            .then(outboundSubResponse => this.processOutboundSubResponse(outboundSubResponse, clientResponse))
            .catch(err => {
                this.log.error('An unexpected error occurred when handling the request.', err);
                clientResponse.status(500).send('SIMULATION_HANDLER_ERROR');
            });
    }

    /**
     * Applies headers received from PingDataGovernance to the client response.
     *
     * @param sidebandHeaders The headers received from the PingDataGovernance Sideband API.
     * @param clientResponse  The client response.
     */
    private applySidebandHeaders(sidebandHeaders: SidebandHeaders, clientResponse: ClientResponse): void {

        // The headers array coming from DG is an array of objects. Here we transform it to map of header names to joined strings
        const joinedHeaders = sidebandHeaders.reduce((acc, header) => {
            const name = Object.keys(header)[0];
            if (!this.shouldIgnoreResponseHeader(name)) {
                return Object.assign(acc, {
                    [name]: acc.hasOwnProperty(name) ? `${acc[name]};${header[name]}` : header[name]
                });
            }
            return acc;
        }, ({} as { [name: string]: string }));

        // After the joined strings have been constructed, apply them to the clientResponse
        Object.keys(joinedHeaders)
            .forEach(name => clientResponse.setHeader(name, joinedHeaders[name]));
    }

    /**
     * Converts client headers to the format expected by the PingDataGovernance Sideband API.
     * 
     * @param clientHeaders The client request headers.
     * 
     * @returns The sideband headers.
     */
    private fromClientHeaders(clientHeaders: string[]): SidebandHeaders {
        const sidebandHeaders: SidebandHeaders = [];
        for (let i = 0; i + 1 < clientHeaders.length; i += 2) {
            const name = clientHeaders[i];
            clientHeaders[i + 1].split(";")
                .forEach(value => sidebandHeaders.push({ [name]: value }));
        }
        return sidebandHeaders;
    }

    /**
     * Converts sub response headers to the format expected by the PingDataGovernance Sideband API.
     *
     * @param subResponseHeaders The sub response headers.
     * 
     * @returns The sideband headers.
     */
    private fromSubResponseHeaders(subResponseHeaders: SubHeaders): SidebandHeaders {
        const sidebandHeaders: SidebandHeaders = [];
        subResponseHeaders.forEach((value, name) => sidebandHeaders.push({ [name]: value }));
        return sidebandHeaders;
    }

    /**
     * Converts headers from the PingDataGovernance Sideband API to sub request format.
     *
     * @param sidebandHeaders The sideband headers.
     * 
     * @returns The sub request headers.
     */
    private asSubRequestHeaders(sidebandHeaders: SidebandHeaders): SubRequestHeaders {
        const fetchHeaders = new SubHeaders();
        (sidebandHeaders || []).forEach(header => {
            const name = Object.keys(header)[0];
            fetchHeaders.append(name, header[name]);
        });
        return fetchHeaders;
    }

    /**
     * Checks if the header name should not be sent back to the client.
     *
     * @param name The header name.
     * 
     * @returns `true` if the header name is protected, `false` otherwise.
     */
    private shouldIgnoreResponseHeader(name: string): boolean {

        // TODO: See if there's a way to prevent the need to strip these headers
        return ['content-encoding', 'transfer-encoding'].reduce((acc, ignoreName) =>
            ignoreName === name.toLowerCase() ? true : acc, false);
    }

    /**
     * Constructor.
     *
     * @param cfg The configuration for the adapter.
     * @param log The logger to use.
     */
    constructor(private cfg: SimulatedSidebandAdapterConfig, private log: Console) { }

    /**
     * Starts the server.
     */
    start(): void {
        this.server = express();

        // Use the JSON body parser
        this.server.use(express.json());

        // Prevent express from adding its own X-Powered-By header
        this.server.disable('x-powered-by');
        this.server.all('*', (request, response) => this.handleRequest(request, response));
        this.server.listen(this.cfg.gatewayPort, () => this.log.info(`Listening on port ${this.cfg.gatewayPort}...`));
    }
}