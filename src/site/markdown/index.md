## Purpose

This service provides essential functionality to deal with `Locations`, `LocationGroups`
and `TransportUnits`. An example often referred to is a service to move a `TransportUnit`
from a `Location` A to a `Location` B. 

## Resources

[![Build status](https://travis-ci.com/openwms/org.openwms.common.service.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.common.service)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Build

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

## Run

After the binary is built it can be started from command line. By default no other infrastructure services are required to run this service.

```
$ java -jar target/openwms-common-service.jar
```

In a distributed Cloud environment the service configuration is fetched from a central configuration service. This behavior can be 
enabled by activating the Spring Profile `CLOUD`. Additionally it makes sense to enable asynchronous communication that requires [RabbitMQ](https://www.rabbitmq.com)
as an AMQP message broker - just add another profile `ASYNCHRONOUS`. If the latter is not applied all asynchronous AMQP endpoints are 
disabled and the service does not send any events nor does it receive application events from remote services.

```
$ java -jar target/openwms-common-service.jar --spring.profiles.active=CLOUD,ASYNCHRONOUS
```

Now the configuration service is tried to be discovered at service startup. The service fails to start if no instance of the configuration
service is available after a configured amount of retries.

## Release

```
$ ./mvnw deploy -Prelease,gpg
```

### Release Documentation

```
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=ASYNCHRONOUS,TEST
$ ./mvnw site scm-publish:publish-scm
```
