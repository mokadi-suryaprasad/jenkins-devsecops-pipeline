@Library('jenkins-shared-library') _

pipeline {
    agent any

    environment {
        LANGUAGES = ['go', 'html']  // List of languages to process
    }

    stages {
        stage('Clone Repo') {
            steps {
                cloneRepo()
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
            sendNotification(success: true, language: 'all')
        }
        failure {
            sendNotification(success: false, language: 'all')
        }
    }
}

def runPipeline(String language) {
    stage("Run Tests for " + language) {
        steps {
            runTests(language: language)
        }
    }

    stage("SonarQube Analysis for " + language) {
        steps {
            sonarQubeAnalysis(language: language)
        }
    }

    stage("Sonar Quality Gate for " + language) {
        steps {
            sonarQualityGate()
        }
    }

    stage("Build Code for " + language) {
        steps {
            buildCode(language: language)
        }
    }

    stage("Build Docker Image for " + language) {
        steps {
            buildDockerImage(language: language)
        }
    }

    stage("Trivy Scan for " + language) {
        steps {
            trivyScan(language: language)
        }
    }

    stage("Push Docker Image for " + language) {
        steps {
            pushDockerImage(language: language)
        }
    }

    stage("Update Kubernetes for " + language) {
        steps {
            updateKubernetes(language: language)
        }
    }
}
