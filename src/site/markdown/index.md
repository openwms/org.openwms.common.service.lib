# Purpose
This service provides essential functionality to deal with `Locations`, `LocationGroups` and `TransportUnits`. An often referred example is
the ability to move a `TransportUnit` from a `Location` A to a `Location` B. 

[![Build status](https://github.com/openwms/org.openwms.common.service/actions/workflows/main.yml/badge.svg)](https://github.com/openwms/org.openwms.common.service/actions/workflows/main.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Maven central](https://img.shields.io/maven-central/v/org.openwms/org.openwms.common.service)](https://search.maven.org/search?q=a:org.openwms.common.service)
[![Docker pulls](https://img.shields.io/docker/pulls/openwms/org.openwms.common.service)](https://hub.docker.com/r/openwms/org.openwms.common.service)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

![ClassDiagram][1]

# Build
Build a runnable fat jar with the execution of all unit and in-memory database integration tests, but without a required [RabbitMQ](https://www.rabbitmq.com)
server to run: 

```
$ ./mvnw package
```

To also build and run with [RabbitMQ](https://www.rabbitmq.com) support call:

```
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=ASYNCHRONOUS,TEST
```

This requires a [RabbitMQ](https://www.rabbitmq.com) server running locally with default settings.

# Run

## Run On Command Line
After the binary has been built it can be started from command line. By default no other infrastructure services are required to run this
service.

```
$ java -jar target/openwms-common-service-exec.jar
```

In a distributed environment the service configuration is fetched from the central [OpenWMS.org Configuration Service](https://github.com/spring-labs/org.openwms.configuration).
This behavior can be enabled by activating the Spring Profile `DISTRIBUTED`. Additionally it makes sense to enable asynchronous
communication that requires a running [RabbitMQ](https://www.rabbitmq.com) instance - just add another profile `ASYNCHRONOUS`. If the latter
is not applied all asynchronous AMQP endpoints are disabled and the service does not send any events nor does it receive application events
from remote services. The AMQP protocol with the [RabbitMQ](https://www.rabbitmq.com) is currently the only supported message broker. But
switching to others, like [HiveMQ (MQTT)](https://www.hivemq.com) or [Apacha Kafka](https://kafka.apache.org/), is not rocket science.

```
$ java -jar target/openwms-common-service-exec.jar --spring.profiles.active=DISTRIBUTED,ASYNCHRONOUS
```

With these profiles applied the OpenWMS.org Configuration Service is tried to be discovered at service startup. The service fails to start
if no instance of the configuration service is available after a configured amount of retries.

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

[1]: images/class-overview.png
