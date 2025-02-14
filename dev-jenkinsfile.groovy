pipeline {
    agent any
    tools {
        maven 'maven3'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }
    triggers {
        cron('* * * * *')
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
