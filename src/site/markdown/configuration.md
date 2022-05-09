## Configuration
OpenWMS.org defines additional configuration parameters beside the standard Spring Framework ones. All custom parameters are children of the
`owms` property namespace.

|Parameter|Type|Default profile value|Description|
|---------|----|-----------|
|owms.eureka.url|string|http://user:sa@localhost:8761|The base URL of the running Eureka service discovery server, inclusive schema and port|
|owms.eureka.zone|string|http://user:sa@localhost:8761/eureka/|The full Eureka registration endpoint URL|
|owms.service.protocol|string|http|The protocol the service' is accessible from Eureka clients|  
|owms.service.hostname|string|localhost|The hostname the service' is accessible from Eureka clients|
|owms.common.dead-letter.exchange-name|string|dle.common|Exchange for the poison message forwarding|
|owms.common.dead-letter.queue-name|string|common-dle-common|Queue for poison messages bound to the dead-letter exchange|
|owms.common.delete-transport-unit-mode|string|strict|Can be `strict` to delete TransportUnits immediately, or `on-accept` where at least one collaborator must accept removal of the TransportUnit|
|owms.commands.common.tu.exchange-name|string|common.tu.commands|Exchange to send out TU requests|
|owms.commands.common.tu.queue-name|string|common-tu-commands-queue|Queue to receive TU commands and responses from|
|owms.commands.common.tu.routing-key|string|common.tu.command.in.*|Routing key to filter incoming commands|
|owms.events.common.lg.exchange-name|string|common.lg|Exchange to send out updates on LocationGroups and Locations|
|owms.events.common.tu.exchange-name|string|common.tu|Exchange to send out updates on TransportUnits|
|owms.events.common.tut.exchange-name|string|common.tut|Exchange to send out updates on TransportUnitTypes|
