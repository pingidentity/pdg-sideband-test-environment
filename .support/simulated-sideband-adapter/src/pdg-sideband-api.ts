interface AccessToken {
    active?: boolean;
    scope?: string[];
    client_id?: string;
    username?: string;
    token_type?: string;
    exp?: number;
    iat?: number;
    nbf?: number;
    sub?: string;
    aud?: string[];
    iss?: string;
    jti?: string;
}

interface Jwk {
    x5c: string[]
}

/**
 * HTTP headers in the format expected by PingDataGovernance Sideband API.
 */
export type Headers = { [name: string]: string }[];

interface HttpCommon {
    http_version: "1.1";
    headers: Headers;
}

interface HttpRequest extends HttpCommon {
    method: string;
    url: string;
    body?: string;
}

/**
 * An HTTP response in the format expected by the PingDataGovernance Sideband API.
 */
export interface HttpResponse extends HttpCommon {
    response_code: string;
    response_status: string;
    body?: string;
}

interface InboundCommon extends HttpRequest {
    source_ip: string;
    source_port: number;
    client_certificate?: Jwk;
}

/**
 * An inbound request for the PingDataGovernance Sideband API.
 */
export interface InboundRequest extends InboundCommon {
    access_token?: AccessToken;
}

/**
 * An inbound response from the PingDataGovernance Sideband API.
 */
export interface InboundResponse extends InboundCommon {
    state?: string;
    response?: HttpResponse;
}

/**
 * An outbound request for the PingDataGovernance Sideband API.
 */
export interface OutboundRequest extends HttpResponse, HttpRequest {
    request?: InboundRequest;
}

/**
 * An outbound response for the PingDataGovernance Sideband API.
 */
export type OutboundResponse = HttpResponse;