FROM java:8-jre
VOLUME library
ADD target/openwms-common-service.jar app.jar
RUN bash -c 'touch /app.jar'
ENV JAVA_OPTS="-noverify -XX:+UseSerialGC -Xss512k -Dspring.zipkin.enabled=false"
ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /app.jar
