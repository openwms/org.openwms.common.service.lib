# Purpose
This service provides essential functionality to deal with `Locations`, `LocationGroups` and `TransportUnits`. An often referred example is
the ability to move a `TransportUnit` from a `Location` A to a `Location` B. 

![ClassDiagram][1]

## Resources
[![Build status](https://github.com/openwms/org.openwms.common.service.lib/actions/workflows/master-build.yml/badge.svg)](https://github.com/openwms/org.openwms.common.service.lib/actions/workflows/master-build.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service.lib&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service.lib)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](../../../LICENSE)
[![Maven central](https://img.shields.io/maven-central/v/org.openwms/org.openwms.common.service.lib)](https://search.maven.org/search?q=a:org.openwms.common.service.lib)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


# Run
This is a library not a standalone microservice that runs on the JVM. This library is part of the OpenWMS.org Common Service, that is
distributed as Docker image and can be instantiated as Docker container.

## Run as Docker Container
Instead of building the software from the sources and run it as Java program on the JVM it must be pulled as a Docker image from 
[Docker Hub](https://hub.docker.com/repository/docker/openwms/org.openwms.common.service) and started as a Docker container.

```
$ docker run openwms/org.openwms.common.service:latest
```

# Development

## Build
The module depends on OpenWMS.org CORE dependencies and one optional COMMON dependency that is only used in the enterprise version and
offers additional API for the UI.

![MavenDependencies][2]

Build a runnable fat jar with the execution of all unit and integration tests, but without a required [RabbitMQ](https://www.rabbitmq.com)
server to run:
```
$ ./mvnw package
```

To also build and run integration tests against a running [RabbitMQ](https://www.rabbitmq.com) instance call:
```
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=AMQP,TEST
```
This requires a [RabbitMQ](https://www.rabbitmq.com) server running locally with default settings.

## Release Binaries
Releasing a new stable version of the software to [Maven Central](https://search.maven.org/) is usually done by the service maintainers
only. To build an optimized and signed binary version and upload it to the Sonatype Staging Repository call:
```
$ ./mvnw deploy -Prelease,gpg
```
After it is uploaded, Sonatype runs a couple of quality checks before it can be manually released to [Maven Central](https://search.maven.org/).

## Release Documentation
To release the API documentation as a static website manually just call the following two Maven commands. This is integrated into the
automated build pipeline as well.
```
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=AMQP,TEST
$ ./mvnw site scm-publish:publish-scm
```

## Extension Points

### State Change Approval
By extending the interface `org.openwms.common.transport.spi.TransportUnitStateChangeApproval` a custom implementation can hook into the
state change lifecycle and either approve or deny a state change of a `TransportUnit`.

### Movement Approval
By extending the interface `org.openwms.common.transport.spi.TransportUnitMoveApproval` a custom implementation can hook into the logic that
controls movements of `TransportUnits` and can either approve or deny all movements.

[1]: images/class-overview.png
[2]: images/maven-deps.drawio.png
