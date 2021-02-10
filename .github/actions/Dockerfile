# Container image that runs your code
FROM openwms/builder-image:latest
ARG ARGPUSHKEY
ARG ARGGITHUB_RUN_NUMBER
ENV PUSHKEY=$ARGPUSHKEY
ENV GITHUB_RUN_NUMBER=$ARGGITHUB_RUN_NUMBER

COPY . /

CMD ./mvnw site scm-publish:publish-scm \
      -DM2_REPO_DIR=/root/.m2/repository \
      -Dspring.datasource.url=jdbc:postgresql://postgres/testdb \
      -Dspring.datasource.username=KARL \
      -Dspring.datasource.password=KARL \
      -Dspring.rabbitmq.host=rabbitmq \
      -DDB_HOST=postgres \
      -DDB_USERNAME=KARL \
      -DDB_PASSWORD=KARL \
      -Denforcer.skip=true \
      -Dscmuser=${PUSHKEY} \
      -Dscmpublish.pubScmUrl=scm:git:https://${PUSHKEY}@github.com/openwms/org.openwms.common.service.git \
      -DdeveloperConnectionUrl=scm:git:https://${PUSHKEY}@github.com/openwms/org.openwms.common.service.git \
      -Dci.buildNumber=${GITHUB_RUN_NUMBER} \
      -Pschemagen -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

