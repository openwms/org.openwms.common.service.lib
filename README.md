# Purpose

This service provides essential functionality to deal with `Locations`, `LocationGroups`
and `TransportUnits`. An example often referred to is a service to move a `TransportUnit`
from a `Location` A to a `Location` B. 

# Build and Run locally
The service can be built and run locally without any other services.

Build the code: 
```
$ ./mvnw package
```

Run in standalone mode:
```
$ java -jar target/openwms-common-service.jar 
```

Or run with some sample data:
```
$ java -Dspring.profiles.active=DEMO -jar target/openwms-common-service.jar 
```

Or additionally run in a distributed environment with an already running Service Registry, Configuration Server and a RabbitMQ broker:
```
$ java -Dspring.profiles.active=ASYNCHRONOUS,DEMO -jar target/openwms-common-service.jar 
```

# Resources

[![Build status](https://travis-ci.com/openwms/org.openwms.common.service.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.common.service)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**Find further Documentation on [Microservice Website](https://openwms.github.io/org.openwms.common.service)**

