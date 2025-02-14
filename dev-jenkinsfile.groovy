pipeline {
    agent any
    tools {
        maven 'maven3'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
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
    }
}
