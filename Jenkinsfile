pipeline {
    agent any
    environment {
        TOMCAT_VERSION = '9.0.79'
        S3_BUCKET = 'talent-test-code-deploy-devops'
        DEPLOYMENT_GROUP = 'devops-test-code-deploy'
        APPLICATION_NAME = 'testing-app'
        AWS_REGION = 'us-west-2'
    }
    stages {
        stage('Checkout Code') {
            steps {
                echo "Checking out code from SCM..."
                checkout scm
            }
        }
        stage('Download Tomcat') {
            steps {
                echo "Downloading Tomcat..."
                sh """
                wget https://downloads.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.zip -O tomcat.zip
                mkdir -p deploy
                unzip tomcat.zip -d deploy
                rm tomcat.zip
                """
            }
        }
        stage('Build Application') {
            steps {
                echo "Building Maven project..."
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Prepare Deployment Package') {
            steps {
                echo "Preparing deployment package..."
                script {
                    def artifact = findFiles(glob: '**/target/*.war')[0]
                    sh """
                    mv ${artifact.path} deploy/apache-tomcat-${TOMCAT_VERSION}/webapps/
                    cd deploy
                    zip -r app-deployment.zip apache-tomcat-${TOMCAT_VERSION}/
                    """
                }
            }
        }
        stage('Upload to S3') {
            steps {
                withAWS(credentials: 'aws-credentials-id', region: "${AWS_REGION}") {
                    echo "Uploading deployment package to S3..."
                    sh """
                    aws s3 cp deploy/app-deployment.zip s3://${S3_BUCKET}/app-deployment.zip --region ${AWS_REGION}
                    """
                }
            }
        }
        stage('Deploy with CodeDeploy') {
            steps {
                withAWS(credentials: 'aws-credentials-id', region: "${AWS_REGION}") {
                    echo "Triggering AWS CodeDeploy..."
                    sh """
                    aws deploy create-deployment \
                    --application-name ${APPLICATION_NAME} \
                    --deployment-group-name ${DEPLOYMENT_GROUP} \
                    --s3-location bucket=${S3_BUCKET},bundleType=zip,key=app-deployment.zip \
                    --region ${AWS_REGION}
                    """
                }
            }
        }
    }
    post {
        success {
            echo "Deployment completed successfully."
        }
        failure {
            echo "Deployment failed. Please check logs."
        }
    }
}

