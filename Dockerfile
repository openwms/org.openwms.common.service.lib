FROM azul/zulu-openjdk-alpine:11-jre
ADD target/openwms-common-service.jar app.jar
ENV JAVA_OPTS="-noverify -Xmx256m -Xss512k"
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar