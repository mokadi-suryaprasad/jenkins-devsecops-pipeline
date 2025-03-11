pipeline {
    agent any

    environment {
        REGISTRY = "docker.io"
        IMAGE_NAME = "${DOCKER_USERNAME}/${JOB_NAME}"
        IMAGE_TAG = "sha-${GIT_COMMIT.take(7)}"

        // Credentials (stored securely in Jenkins)
        DOCKER_USERNAME = credentials('docker-hub-username')
        DOCKER_PASSWORD = credentials('docker-hub-password')
        SONAR_TOKEN = credentials('sonarqube-token')
        SONAR_HOST_URL = "http://your-sonarqube-server"
        SLACK_WEBHOOK = credentials('slack-webhook')
    }

    stages {
        stage('Checkout Code') {
            steps {
                script {
                    echo "Checking out source code..."
                    checkout scm
                }
            }
        }

        stage('Unit Testing') {
            steps {
                script {
                    echo "Running Unit Tests..."
                    sh "npm install"
                    sh "npm test"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "Running SonarQube Analysis..."
                    sh """
                        sonar-scanner \
                        -Dsonar.projectKey=my-project-key \
                        -Dsonar.organization=my-org \
                        -Dsonar.sources=./src \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Check SonarQube Quality Gate') {
            steps {
                script {
                    echo "Checking SonarQube Quality Gate..."
                    def status = sh(script: """
                        curl -s -X GET "${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=my-project-key" \
                        -H "Authorization: Bearer ${SONAR_TOKEN}" | jq -r '.projectStatus.status'
                    """, returnStdout: true).trim()

                    if (status != 'OK') {
                        error "SonarQube Quality Gate Failed!"
                    }
                }
            }
        }

        stage('Build & Store Artifacts') {
            steps {
                script {
                    echo "Building the project..."
                    sh "npm run build"
                    stash includes: 'dist/**', name: 'build-artifacts'
                }
            }
        }

        stage('Docker Image Build') {
            steps {
                script {
                    echo "Building Docker Image..."
                    sh "docker build -t $REGISTRY/$IMAGE_NAME:$IMAGE_TAG ."
                }
            }
        }

        stage('Security Scanning with Trivy') {
            steps {
                script {
                    echo "Running Trivy Security Scan..."
                    sh "trivy image --exit-code 1 --severity CRITICAL $REGISTRY/$IMAGE_NAME:$IMAGE_TAG || exit 1"
                }
            }
        }

        stage('Docker Image Push') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'docker-hub-credentials', url: ""]) {
                        echo "Pushing Docker Image..."
                        sh "docker push $REGISTRY/$IMAGE_NAME:$IMAGE_TAG"
                    }
                }
            }
        }

        stage('Update Kubernetes Deployment') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "Updating Kubernetes deployment..."
                    sh """
                        kubectl set image deployment/my-app my-app=$REGISTRY/$IMAGE_NAME:$IMAGE_TAG --record
                    """
                }
            }
        }
    }

    post {
        success {
            script {
                echo "Pipeline completed successfully!"
                sh """
                    curl -X POST -H 'Content-type: application/json' \
                    --data '{"text":"✅ CI/CD Pipeline completed successfully!"}' \
                    ${SLACK_WEBHOOK}
                """
            }
        }
        failure {
            script {
                echo "Pipeline failed!"
                sh """
                    curl -X POST -H 'Content-type: application/json' \
                    --data '{"text":"❌ CI/CD Pipeline Failed! Check logs for details."}' \
                    ${SLACK_WEBHOOK}
                """
            }
        }
    }
}
