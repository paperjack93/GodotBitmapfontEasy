# EXTW-INTEG

A service for the external wallet API and operator required endpoints.

# configuration

When running a local instance the application.properies configuration is read from the src/main/resources/ folder. The deployed configuration
is sourced from /charts/extw-integ/ (TODO: support one configuration per environment) and copied to the Docker image /deployments/config folder.

The /etc folder is unused and only temporary for legacy reference.

# project structure

## File structure
`src/main/docker`
Docker files for different compile settings. Dockerfile.jvm is currently in use.

`src/main/java/com/dashur/integration/commons`
The Dashur API implementation 

`src/main/java/com/dashur/integration/extw/connectors`
The operator specific integration code 

## operator connector
A connector has four major component classes

The endpoints implemented by the operator `*clientService.java`
Configuration `*Configuration.java`
Dashur required endpoints `*ConnectorServiceImpl.java`
Operator required endpoints `*Controller.java`

The classes that define the communication data can be found in the operator `data` folder.

# Redis server and Pro Redisson

A local redis server for caching tokens must be set up and configured in application.properties. The client implementation
in extw-integ uses Pro Redisson and requires a license.

# code-with-quarkus Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources) 
