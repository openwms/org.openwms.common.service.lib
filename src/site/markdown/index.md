# Purpose

This service provides essential functionality to deal with `Locations`, `LocationGroups`
and `TransportUnits`. An example often referred to is a service to move a `TransportUnit`
from a `Location` A to a `Location` B. 

[![Build status](https://travis-ci.com/openwms/org.openwms.common.service.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.common.service)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Build

Build a runnable fat jar with execution of all unit and in-memory database integrations, but without a [RabbitMQ](https://www.rabbitmq.com)
server required to run: 

```
$ ./mvnw package
```

To also build and run with [RabbitMQ](https://www.rabbitmq.com) support call:

```
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=ASYNCHRONOUS,TEST
```

But notice that this requires a [RabbitMQ](https://www.rabbitmq.com) server running locally with default settings.

# Run

## Run On Command Line
After the binary is built it can be started on the JVM from command line. By default no other infrastructure services are required to run
this service.

```
$ java -jar target/openwms-common-service.jar
```

In a distributed Cloud environment the service configuration is fetched from the central [OpenWMS.org Configuration Service](https://github.com/spring-labs/org.openwms.configuration).
This behavior can be enabled by activating the Spring Profile `CLOUD`. Additionally it makes sense to enable asynchronous communication that
requires a running [RabbitMQ](https://www.rabbitmq.com) instance - just add another profile `ASYNCHRONOUS`. If the latter is not applied all
asynchronous AMQP endpoints are disabled and the service does not send any events nor does it receive application events from remote
services.

```
$ java -jar target/openwms-common-service.jar --spring.profiles.active=CLOUD,ASYNCHRONOUS
```

Now the configuration service is tried to be discovered at service startup. The service fails to start if no instance of the configuration
service is available after a configured amount of retries.

## Run as Docker Container
Instead of building the software from the sources and run it as Java program on the JVM it can also be fetched as a Docker image from 
[Docker Hub](https://hub.docker.com/repository/docker/openwms/org.openwms.common.service) and started as a Docker container.

```
$ docker run openwms/org.openwms.common.service:latest
```

# Release
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
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=ASYNCHRONOUS,TEST
$ ./mvnw site scm-publish:publish-scm
```
