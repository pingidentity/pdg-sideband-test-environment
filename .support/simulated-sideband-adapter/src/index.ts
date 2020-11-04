import util from 'util';
import fs from "fs";
import { SimulatedSidebandAdapter, SimulatedSidebandAdapterConfig } from "./simulated-sideband-adapter";

// Allow insecure http requests
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const configFile = "./assets/config.json";

util.promisify(fs.readFile)(configFile)
    .then(content => content.toString())
    .then(JSON.parse)
    .then(cfg => Object.assign(cfg, {
        requestContextMethod: process.env.PDG_SIDEBAND_REQUEST_CONTEXT_METHOD
    }) as SimulatedSidebandAdapterConfig)
    .then(cfg => new SimulatedSidebandAdapter(cfg, console))
    .then(simulation => simulation.start())
    .catch(e => {
        console.error('An error occurred while starting the simulated sideband adapter.', e);
        process.exit(1);
    });