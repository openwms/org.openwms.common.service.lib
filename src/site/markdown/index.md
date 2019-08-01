# OpenWMS.org COMMON: Services

## Purpose

This service provides essential functionality to deal with `Locations`, `LocationGroups`
and `TransportUnits`. An example often referred to is a service to move a `TransportUnit`
from a `Location` A to a `Location` B. 

## Resources

[![Build status](https://travis-ci.com/openwms/org.openwms.common.service.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.common.service)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

- Documentation on [Microservice Website](https://openwms.github.io/org.openwms.common.service)

## Build

Build a runable fat jar:

```
$ mvn package
```

Run the Sonar analysis:

```
$ mvn package -Psonar
```

## Run

After the binary is built it can be started from command line. By default no other infrastructure services are required to run this service.

```
$ java -jar target/openwms-common-service-exec.jar
```

In a distributed Cloud environment the service configuration is fetched from a centralized configuration service. This behavior can be 
enabled by activating the Spring Profile `CLOUD`:

```
$ java -jar target/openwms-common-service-exec.jar --spring.profiles.active=CLOUD
```

Now the configuration service is tried to be discovered at service startup. The service fails to start if no instance of the configuration
service is available after retrying a configured amount of times.

## Release

```
$ mvn deploy -Prelease,gpg
```

### Release Documentation

```
$ mvn package -Psonar
$ mvn site scm-publish:publish-scm
```
