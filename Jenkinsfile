@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Build and Deploy Backend') {
            steps {
                ciPipeline(language: 'go')
            }
        }

        stage('Deploy Frontend') {
            steps {
                ciPipeline(language: 'html')
            }
        }
    }
}
