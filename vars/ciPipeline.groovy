def call(Map config = [:]) {
    pipeline {
        agent any
        environment {
            SONAR_HOST_URL = credentials('sonar-host-url')
            SONAR_TOKEN = credentials('sonar-token')
            DOCKER_USERNAME = credentials('docker-username')
            DOCKER_PASSWORD = credentials('docker-password')
            SLACK_WEBHOOK = credentials('slack-webhook')
            GITHUB_TOKEN = credentials('github-pat')
            IMAGE_NAME = "${DOCKER_USERNAME}/${config.language}-app:${env.BUILD_NUMBER}"
            GIT_REPO = "https://${GITHUB_TOKEN}@github.com/mokadi-suryaprasad/jenkins-devsecops-pipeline.git"
            GIT_BRANCH = 'main'
            SONAR_PROJECT_KEY = "my-${config.language}-project"
        }

        stages {
            stage('Clone GitHub Repository') {
                steps {
                    script {
                        echo "Cloning repository from GitHub"
                        sh '''
                            rm -rf workspace
                            git clone -b $GIT_BRANCH $GIT_REPO workspace
                            cd workspace
                            git config --global --add safe.directory $(pwd)
                        '''
                    }
                }
            }

            stage('Run Tests') {
                steps {
                    script {
                        dir('workspace') {
                            if (config.language == 'go') {
                                echo "Running Go tests"
                                sh 'go test ./...'
                            } else {
                                echo "Skipping tests for HTML as it is static"
                            }
                        }
                    }
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    script {
                        dir('workspace') {
                            def sonarFile = config.language == 'go' ? 'resources/sonar-project-go.properties' : 'resources/sonar-project-html.properties'
                            echo "Running SonarQube Analysis"
                            sh "sonar-scanner -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dproject.settings=${sonarFile} -Dsonar.login=${SONAR_TOKEN}"
                        }
                    }
                }
            }

            stage('Sonar Quality Gate') {
                steps {
                    script {
                        def status = sh(
                            script: """
                            curl -s -u $SONAR_TOKEN: "$SONAR_HOST_URL/api/qualitygates/project_status?projectKey=$SONAR_PROJECT_KEY" | jq -r '.projectStatus.status'
                            """, returnStdout: true
                        ).trim()

                        if (status != "OK") {
                            error "SonarQube Quality Gate Failed"
                        }
                    }
                }
            }

            stage('Build Code') {
                steps {
                    script {
                        dir('workspace') {
                            if (config.language == 'go') {
                                echo "Building Go application"
                                sh 'go build -o app'
                            } else {
                                echo "Skipping build step for HTML"
                            }
                        }
                    }
                }
            }

            stage('Build Docker Image') {
                steps {
                    script {
                        dir('workspace') {
                            def dockerfilePath = config.language == 'go' ? 'backend/Dockerfile' : 'frontend/Dockerfile'
                            echo "Building Docker image: $IMAGE_NAME"
                            sh "docker build -t $IMAGE_NAME -f $dockerfilePath ."
                        }
                    }
                }
            }

            stage('Trivy Scan Docker Image') {
                steps {
                    script {
                        echo "Running Trivy vulnerability scan on $IMAGE_NAME"
                        sh '''
                            trivy image --exit-code 1 --severity CRITICAL,HIGH $IMAGE_NAME || {
                                echo "Critical vulnerabilities found!";
                                exit 1;
                            }
                        '''
                    }
                }
            }

            stage('Push Docker Image') {
                steps {
                    script {
                        echo "Logging into Docker Hub and pushing the image"
                        sh '''
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                            docker push $IMAGE_NAME
                        '''
                    }
                }
            }

            stage('Update Kubernetes Deployment') {
                steps {
                    script {
                        dir('workspace') {
                            def deploymentFile = config.language == 'go' ? 'kubernetes/backend-deployment.yaml' : 'kubernetes/frontend-deployment.yaml'
                            echo "Updating Kubernetes deployment.yaml with new image"
                            sh '''
                                yq eval '.spec.template.spec.containers[0].image = "$IMAGE_NAME"' -i $deploymentFile
                                git add $deploymentFile
                                git commit -m "Update deployment.yaml with image $IMAGE_NAME" || echo "No changes to commit"
                                git push origin $GIT_BRANCH || echo "Git push failed. Please check permissions."
                            '''
                        }
                    }
                }
            }
        }

        post {
            success {
                script {
                    echo "Pipeline successful, sending Slack notification"
                    slackSend(color: 'good', message: "${config.language} pipeline executed successfully: ${env.BUILD_URL}")
                }
            }
            failure {
                script {
                    echo "Pipeline failed, sending Slack notification"
                    slackSend(color: 'danger', message: "${config.language} pipeline failed: ${env.BUILD_URL}")
                }
            }
        }
    }
}
