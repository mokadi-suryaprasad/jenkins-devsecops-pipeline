# Jenkins Shared Library for DevSecOps CI/CD Pipeline

## Overview
This repository contains a **Jenkins Shared Library** to automate CI/CD pipelines for **Go and HTML applications**.  
It includes **stages for testing, security scanning, Docker image building, and deployment to Kubernetes**.

## Folder Structure
```
jenkins-shared-library
│
├── vars                # Contains pipeline function scripts
│   ├── cloneRepo.groovy
│   ├── runTests.groovy
│   ├── sonarQubeAnalysis.groovy
│   ├── sonarQualityGate.groovy
│   ├── buildCode.groovy
│   ├── buildDockerImage.groovy
│   ├── trivyScan.groovy
│   ├── pushDockerImage.groovy
│   ├── updateKubernetes.groovy
│   ├── sendNotification.groovy
│
├── resources           # Contains supporting files
│   ├── sonar-project.properties
│
├── library.groovy         # Entry point for the Shared Library
├── README.md              # Documentation
```

## How to Use This Library

### Add the Shared Library in Jenkins
1. Navigate to **Manage Jenkins -> Configure System -> Global Pipeline Libraries**
2. Add a new library with the following details:
   - **Library Name**: `jenkins-shared-library`
   - **Default Version**: `main`
   - **Allow default version to be overridden**: Checked
   - **Load implicitly**: Unchecked

### Use the Library in a Jenkinsfile
```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Clone Repository') {
            steps {
                cloneRepo()
            }
        }
        stage('Run Tests') {
            steps {
                runTests(language: 'go')
            }
        }
    }
}
```

### Commit and Push Changes
```sh
git add .
git commit -m "Added Jenkins Shared Library"
git push origin main
```

## Available Functions
Each function is stored inside the `vars/` directory and can be used inside your **Jenkinsfile**.

| Function | Description |
|----------|-------------|
| `cloneRepo()` | Clones the GitHub repository |
| `runTests(language: 'go' or 'html')` | Runs tests for the selected language |
| `sonarQubeAnalysis(language: 'go' or 'html')` | Performs SonarQube analysis |
| `sonarQualityGate()` | Checks SonarQube quality gate results |
| `buildCode(language: 'go' or 'html')` | Builds the application (only Go requires build) |
| `buildDockerImage(language: 'go' or 'html')` | Builds a Docker image for the app |
| `trivyScan(language: 'go' or 'html')` | Runs Trivy security scans on the Docker image |
| `pushDockerImage(language: 'go' or 'html')` | Pushes the Docker image to Docker Hub |
| `updateKubernetes(language: 'go' or 'html')` | Updates the Kubernetes deployment file |
| `sendNotification(success: true/false, language: 'go' or 'html')` | Sends a Slack notification |



