@Library('jenkins-shared-library') _

pipeline {
    agent any
    environment {
        GIT_REPO = 'https://github.com/mokadi-suryaprasad/jenkins-devsecops-pipeline.git'
        GIT_BRANCH = 'main'
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    echo "Cloning Repository from GitHub"
                    sh """
                        rm -rf workspace
                        git clone -b $GIT_BRANCH $GIT_REPO workspace
                        cd workspace
                        git config --global --add safe.directory ${sh(script: 'pwd', returnStdout: true).trim()}
                    """
                }
            }
        }

        stage('Backend - Build & Deploy') {
            steps {
                script {
                    echo "Running Backend CI/CD Pipeline for Go"
                    ciPipeline(language: 'go')
                }
            }
        }

        stage('Frontend - Deploy') {
            steps {
                script {
                    echo "Running Frontend CI/CD Pipeline for HTML"
                    ciPipeline(language: 'html')
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline executed successfully!"
        }
        failure {
            echo "Pipeline failed. Check logs for errors."
        }
    }
}
