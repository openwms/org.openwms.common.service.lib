FROM adoptopenjdk/openjdk11-openj9:jre-11.0.7_10_openj9-0.20.0-alpine as builder
WORKDIR application
ARG JAR_FILE=target/openwms-common-service-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk/openjdk11-openj9:jre-11.0.7_10_openj9-0.20.0-alpine
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Dowms.common.barcode.length=10 -Xshareclasses -Xquickstart -noverify $JAVA_OPTS", "org.springframework.boot.loader.JarLauncher"]