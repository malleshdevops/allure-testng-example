pipeline {
    agent {
        label 'slave1'
    }
    tools {
        maven 'maven3'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }
    triggers {
        cron('59 * * * *')
    }
    stages {
        stage('build') {
            steps {
                sh '''
               echo "triggering maven build"
               mvn clean package
               '''
            }
        }
        stage("allure report"){
            steps{
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }
        stage('archive'){
          steps{
              archive 'target/*.jar'
          }
        }
        stage('finger prints'){
            steps{
                fingerprint 'target/*.jar'
            }
        }
    }
}
