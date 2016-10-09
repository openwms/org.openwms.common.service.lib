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
   stage('Results') {

      archive 'target/*.jar'
   }
}