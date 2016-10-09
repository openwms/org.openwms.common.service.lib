#!groovy

node {
   def mvnHome
   stage('Preparation') {

      git 'git@github.com:spring-labs/org.openwms.services.git'
      mvnHome = tool 'M3'
   }
   stage('Build') {

      sh "'${mvnHome}/bin/mvn' clean package -U"
   }
   stage('Publish') {
      sh "git remote add heroku git@heroku.com:openwms-services.git"
      sh "git push heroku master"
   }
   stage('Results') {

      archive 'target/*.jar'
   }
}