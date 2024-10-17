pipeline {
    agent any
    tools { 
        maven 'maven3'
    }
    stages {
        stage('clone repo') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins-ssh-id', url: 'git@github.com:malleshdevops/allure-testng-example.git']])
            }
        }
        stage('maven build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('allure report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }
    }
}
