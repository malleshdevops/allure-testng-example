pipeline {
    agent { 
        node { 
        label 'master' 
        customWorkspace "/tmp/jenkins-workspace"   
        }
        }
    options { buildDiscarder(logRotator(numToKeepStr: '5')) 
    }
   
    tools {
        maven 'maven' 
    }
    stages {
        stage('github') {
            
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'devops14-jenkins-ssh-id', url: 'git@github.com:malleshdevops/allure-testng-example.git']])
            }
        }
        stage('maven build'){
            steps{
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            sh '''
              cd ${WORKSPACE}
              mvn clean package
            '''
        }
                
            }
            
        }
        stage('allure report'){
            steps{
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }
        stage('email notification'){
            steps{
                emailext attachLog: true, body: 'This is test email for devops15', subject: 'Devops15-Email-Jenkins-Build-Failure: $JOB_NAME', to: 'malleshdevops2021@gmail.com,devops14talent@outlook.com'
            }
        }
        
    }
}
