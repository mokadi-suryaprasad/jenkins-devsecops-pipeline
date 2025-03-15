@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build and Deploy Backend') {
            steps {
                script {
                    echo "Starting Backend Pipeline for Go"
                    ciPipeline(language: 'go')
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                script {
                    echo "Starting Frontend Pipeline for HTML"
                    ciPipeline(language: 'html')
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
