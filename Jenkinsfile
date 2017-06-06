#!groovy

node {
    def mvnHome
    stage('\u27A1 Preparation') {
      git 'git@github.com:spring-labs/org.openwms.services.git'
      mvnHome = tool 'M3'
      cmdLine = '-Dci.buildNumber=${BUILD_NUMBER} -Ddocumentation.dir=${WORKSPACE}/target'
    }
    stage('\u27A1 Build') {
      configFileProvider(
          [configFile(fileId: 'maven-local-settings', variable: 'MAVEN_SETTINGS')]) {
            sh "'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS clean install ${cmdLine} -Psonatype -U"
      }
    }
    stage('\u27A1 Heroku Staging') {
      sh '''
          if git remote | grep heroku > /dev/null; then
             git remote rm heroku
          fi
          git remote add heroku https://:${HEROKU_API_KEY}@git.heroku.com/openwms-services.git
          git push heroku master -f
      '''
    }
    stage('\u27A1 Results') {
      archive 'target/*.jar'
    }
}