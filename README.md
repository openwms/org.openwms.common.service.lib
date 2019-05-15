# Purpose

This service provides essential functionality to deal with `Locations`, `LocationGroups`
and `TransportUnits`. An example often referred to is a service to move a `TransportUnit`
from a `Location` A to a `Location` B. 

# Resources

[![Build status](https://travis-ci.com/openwms/org.openwms.common.service.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.common.service)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

- Documentation on [GitHub Wiki Pages](https://github.com/openwms/org.openwms.common.service/wiki)
- Generated Microservice Website with Javadoc and API docs: [here](https://www.interface21.io/docs/common/base/index.html)

# Release

```
$ mvn deploy -Prelease,gpg
```
