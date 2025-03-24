@Library('jenkins-shared-library') _

pipeline {
    agent any

    environment {
        LANGUAGES = ['go', 'html']  // List of languages to process
    }

    stages {
        stage('Clone Repo') {
            steps {
                script {
                    cloneRepo()
                }
            }
        }

        stage('Run Pipelines for Both Go and HTML') {
            parallel {
                stage('Backend Go Pipeline') {
                    steps {
                        script {
                            runPipeline('go')
                        }
                    }
                }
                stage('Frontend HTML Pipeline') {
                    steps {
                        script {
                            runPipeline('html')
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                sendNotification(success: true, language: 'all')
            }
        }
        failure {
            script {
                sendNotification(success: false, language: 'all')
            }
        }
    }
}

// Ensure this function is within script block
script {
    def runPipeline(String language) {
        stage("Run Tests for ${language}") {
            runTests(language: language)
        }

        stage("SonarQube Analysis for ${language}") {
            sonarQubeAnalysis(language: language)
        }

        stage("Sonar Quality Gate for ${language}") {
            sonarQualityGate()
        }

        stage("Build Code for ${language}") {
            buildCode(language: language)
        }

        stage("Build Docker Image for ${language}") {
            buildDockerImage(language: language)
        }

        stage("Trivy Scan for ${language}") {
            trivyScan(language: language)
        }

        stage("Push Docker Image for ${language}") {
            pushDockerImage(language: language)
        }

        stage("Update Kubernetes for ${language}") {
            updateKubernetes(language: language)
        }
    }
}
