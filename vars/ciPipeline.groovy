def call(Map config = [:]) {
    pipeline {
        agent any
        environment {
            SONAR_HOST_URL = credentials('sonar-host-url')
            SONAR_TOKEN = credentials('sonar-token')
            DOCKER_USERNAME = credentials('docker-username')
            DOCKER_PASSWORD = credentials('docker-password')
            SLACK_WEBHOOK = credentials('slack-webhook')
            IMAGE_NAME = "${env.DOCKER_USERNAME}/${env.JOB_NAME}:${env.BUILD_NUMBER}"
        }

        stages {
            stage('Checkout Code') {
                steps {
                    checkout scm
                }
            }

            stage('Run Tests') {
                steps {
                    script {
                        if (config.language == 'go') {
                            sh 'go test ./...'
                        } else if (config.language == 'html') {
                            echo 'Skipping tests for HTML as it is static'
                        }
                    }
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    script {
                        if (config.language == 'go') {
                            sh 'sonar-scanner -Dproject.settings=resources/sonar-project-go.properties'
                        } else if (config.language == 'html') {
                            sh 'sonar-scanner -Dproject.settings=resources/sonar-project-html.properties'
                        }
                    }
                }
            }

            stage('Sonar Quality Gate') {
                steps {
                    script {
                        def status = sh(script: '''
                            curl -s -X GET "$SONAR_HOST_URL/api/qualitygates/project_status?projectKey=my-project" \
                            -H "Authorization: Bearer $SONAR_TOKEN" | jq -r '.projectStatus.status'
                        ''', returnStdout: true).trim()
                        
                        if (status != "OK") {
                            error "SonarQube Quality Gate Failed"
                        }
                    }
                }
            }

            stage('Build Code') {
                steps {
                    script {
                        if (config.language == 'go') {
                            sh 'go build -o app'
                        } else if (config.language == 'html') {
                            echo 'Skipping build step for HTML'
                        }
                    }
                }
            }

            stage('Build Docker Image') {
                steps {
                    script {
                        sh """
                            echo "Building Docker image: $IMAGE_NAME"
                            docker build -t $IMAGE_NAME .
                        """
                    }
                }
            }

            stage('Trivy Scan Docker Image') {
                steps {
                    script {
                        sh """
                            echo "Checking for Trivy installation..."
                            if ! command -v trivy &> /dev/null; then
                                echo "Installing Trivy..."
                                wget -qO- https://github.com/aquasecurity/trivy/releases/latest/download/trivy-linux-amd64.tar.gz | tar xz
                                sudo mv trivy /usr/local/bin/
                            fi

                            echo "Running Trivy vulnerability scan on $IMAGE_NAME..."
                            trivy image --exit-code 1 --severity CRITICAL,HIGH $IMAGE_NAME || {
                                echo "Trivy scan found vulnerabilities! Failing the build.";
                                exit 1;
                            }
                        """
                    }
                }
            }

            stage('Push Docker Image') {
                steps {
                    script {
                        sh """
                            echo "Logging into Docker Hub..."
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                            echo "Pushing Docker image: $IMAGE_NAME"
                            docker push $IMAGE_NAME
                        """
                    }
                }
            }

            stage('Update Kubernetes Deployment') {
                steps {
                    script {
                        sh """
                            echo "Updating Kubernetes deployment.yaml with new image..."
                            yq eval '.spec.template.spec.containers[0].image = \"$IMAGE_NAME\"' -i kubernetes/deployment.yaml
                            
                            git add kubernetes/deployment.yaml
                            git commit -m "Updated deployment.yaml with $IMAGE_NAME"
                            git push || echo "Warning: Git push failed. Please check permissions."
                        """
                    }
                }
            }
        }

        post {
            success {
                script {
                    echo "Pipeline execution successful! Sending Slack notification..."
                    slackSend(color: 'good', message: "✅ Pipeline executed successfully: ${env.BUILD_URL}")
                }
            }
            failure {
                script {
                    echo "Pipeline failed! Sending Slack notification..."
                    slackSend(color: 'danger', message: "❌ Pipeline failed: ${env.BUILD_URL}")
                }
            }
        }
    }
}
