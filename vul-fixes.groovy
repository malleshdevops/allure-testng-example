pipeline {
    agent any
    
      environment {
        GH_TOKEN = credentials('github-token')
        REPO_URL = sh(script: 'git config --get remote.origin.url', returnStdout: true).trim()
        REPO_OWNER = sh(script: "basename \$(dirname \$(git config --get remote.origin.url))", returnStdout: true).trim()
        REPO_NAME = sh(script: "basename -s .git \$(git config --get remote.origin.url)", returnStdout: true).trim()
        CSV_FILE = 'vulnerability_report.csv'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    BRANCH = "fix-vulnerabilities-${UUID.randomUUID().toString().take(6)}"
                    env.BRANCH = BRANCH
                }
            }
        }
        stage('Checkout Code') {
            steps {
                git branch: 'master', url: "${REPO_URL}"
            }
        }
       stage('Install Dependencies') {
            steps {
                script {
                    sh '''
                    if ! command -v trivy &> /dev/null; then
                        echo "Installing Trivy..."
                        curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin
                    fi
                    if ! command -v snyk &> /dev/null; then
                        echo "Installing Snyk..."
                        npm install -g snyk
                    fi
                    '''
                }
            }
        }
        stage('Scan for Vulnerabilities') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --format json your-docker-image:latest > trivy-report.json'
                sh 'snyk test --json > snyk-report.json'
                sh 'cp trivy-report.json before_fix_trivy.json'
                sh 'cp snyk-report.json before_fix_snyk.json'
            }
        }

        stage('Analyze and Fix Vulnerabilities') {
            steps {
                script {
                    def trivyReport = readFile('trivy-report.json')
                    def snykReport = readFile('snyk-report.json')

                    if (trivyReport.contains('"VulnerabilityID"') || snykReport.contains('"vulnerabilities"')) {
                        echo "Vulnerabilities found! Attempting to fix..."
                        sh 'python fix_vulnerabilities.py'
                    } else {
                        echo "No critical vulnerabilities found."
                    }
                }
            }
        }

        stage('Generate Report') {
            steps {
                script {
                    sh 'trivy image --severity HIGH,CRITICAL --format json your-docker-image:latest > after_fix_trivy.json'
                    sh 'snyk test --json > after_fix_snyk.json'
                    sh 'python generate_report.py ${CSV_FILE} ${BUILD_NUMBER}'
                }
            }
        }

        stage('Commit & Push Fixes') {
            steps {
                script {
                    sh "git checkout -b ${BRANCH}"
                    sh "git add ."
                    sh "git commit -m 'Auto-fix vulnerabilities [Jenkins Build: ${BUILD_NUMBER}]'"
                    sh "git push origin ${BRANCH}"
                }
            }
        }

        stage('Create GitHub PR') {
            steps {
                sh '''
                curl -X POST -H "Authorization: token $GH_TOKEN" \
                -d '{
                    "title": "Automated Vulnerability Fix [Jenkins Build: ${BUILD_NUMBER}]",
                    "head": "'${BRANCH}'",
                    "base": "main",
                    "body": "This PR fixes vulnerabilities detected by Jenkins build ${BUILD_NUMBER}."
                }' https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/pulls
                '''
            }
        }

        stage('PR Review & Merge') {
            steps {
                script {
                    sleep(10)
                    sh 'python auto_merge_pr.py'
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                sh 'deploy.sh'
            }
        }
    }

    post {
        always {
            echo 'Pipeline Execution Complete'
        }
    }
}
