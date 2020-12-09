FROM node:14.11.0-alpine3.12

COPY . /app
WORKDIR /app
RUN npm install
ENTRYPOINT [ "npm", "run", "--silent", "start" ]
CMD ["sideband-adapter"]
