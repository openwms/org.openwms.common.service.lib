#!groovy

node {
  try {
    def mvnHome
    stage('\u27A1 Preparation') {
      git 'git@github.com:spring-labs/org.openwms.services.git'
      mvnHome = tool 'M3'
    }
    stage('\u27A1 Build') {
      sh "'${mvnHome}/bin/mvn' clean install -Psordocs,sonatype -U"
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
    stage('\u27A1 Sonar') {
      sh "'${mvnHome}/bin/mvn' clean org.jacoco:jacoco-maven-plugin:prepare-agent sonar:sonar -Djacoco.propertyName=jacocoArgLine -Pjenkins"
    }
  } finally {
    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
  }
}