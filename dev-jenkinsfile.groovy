pipeline {
    agent any
    tools{
       maven 'maven3' 
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
