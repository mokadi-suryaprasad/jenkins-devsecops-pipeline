@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build and Deploy') {
            parallel {
                stage('Backend - Go') {
                    steps {
                        script {
                            echo "Starting Backend Pipeline for Go"
                            ciPipeline(language: 'go')
                        }
                    }
                }

                stage('Frontend - HTML') {
                    steps {
                        script {
                            echo "Starting Frontend Pipeline for HTML"
                            ciPipeline(language: 'html')
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed! Please check the logs."
        }
    }
}
