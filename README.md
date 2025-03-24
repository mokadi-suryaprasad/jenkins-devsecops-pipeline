# DevSecOps CI/CD Pipeline

## Overview
This project is a **DevSecOps CI/CD pipeline** that automates building, testing, security scanning, and deploying applications using **Jenkins, Docker, SonarQube, Trivy, and ArgoCD**. It ensures a secure and efficient software delivery process.

## How It Works
The pipeline follows a step-by-step process:

1. **Code Commit:** Developers push their code to a Git repository (GitHub/GitLab).
2. **Unit Testing:** Runs tests to check if the code works properly.
3. **Static Code Analysis:** Uses SonarQube to analyze the code quality.
4. **Build Application:** Compiles and builds the application using NPM or Go.
5. **Docker Image Creation:** Builds a Docker image of the application.
6. **Security Scanning:** Uses Trivy to scan for vulnerabilities in the Docker image.
7. **Push to Docker Hub:** If the scan is successful, the image is pushed to Docker Hub.
8. **Update Image Tag:** Updates the deployment file with the new image version.
9. **Deploy to Kubernetes:** Uses ArgoCD to deploy the application to Kubernetes.
10. **Monitoring & Notifications:** Sends notifications about the pipeline status (success or failure).

## Pipeline Diagram
Below is a visual representation of the DevSecOps pipeline:

## Technologies Used
- **Jenkins** - Automates CI/CD pipeline execution
- **GitHub/GitLab** - Version control system
- **SonarQube** - Code quality analysis
- **Trivy** - Security scanning for vulnerabilities
- **Docker** - Containerization
- **Kubernetes (K8s)** - Container orchestration
- **ArgoCD** - Kubernetes deployment automation

## How to Set Up the Pipeline

### 1. Clone the Repository
```sh
git clone https://github.com/your-repo/devsecops-pipeline.git
cd devsecops-pipeline
```

### 2. Configure Jenkins
- Install Jenkins and required plugins.
- Add a **Jenkins Shared Library** for reusable pipeline functions.
- Configure **SonarQube** and **Trivy** in Jenkins.

### 3. Connect Jenkins to GitHub/GitLab
- Add a webhook to trigger the pipeline on code changes.

### 4. Run the Pipeline
- Create a **Jenkinsfile** in your project.
- Run the Jenkins job to start the CI/CD process.

### 5. Verify Deployment
- Check ArgoCD to see the deployed application in Kubernetes.
- Monitor logs and test the running application.

## Contributing
We welcome contributions! Feel free to open issues or submit pull requests.


# DevSecOps CI/CD Pipeline Setup for Go and HTML Applications
This guide provides step-by-step instructions for setting up a DevSecOps pipeline using Jenkins, Docker, AWS CLI, Ansible, Terraform, Trivy, and SSL Certificates.

## 1. Update System & Install Required Packages
```bash
sudo apt update && sudo apt install -y unzip jq net-tools
```
- **unzip** → Extracts files
- **jq** → JSON parser for automation
- **net-tools** → Networking utilities

## 2. Install Go & HTML Prerequisites
### Install Go
```bash
wget https://go.dev/dl/go1.22.0.linux-amd64.tar.gz
sudo tar -xvf go1.22.0.linux-amd64.tar.gz -C /usr/local
export PATH=$PATH:/usr/local/go/bin
```

### Install HTML Prerequisites (Node.js and npm)
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
sudo npm install -g http-server
```
- **Node.js** and **npm** are used for HTML applications and frontend management.

## 3. Install Docker & Configure User Permissions
```bash
curl https://get.docker.com | bash
sudo useradd -G docker adminsurya
sudo usermod -aG docker adminsurya
```
- Installs Docker
- Adds **adminsurya** to the Docker group

## 4. Install AWS CLI
```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```
- AWS CLI is used for cloud automation.

## 5. Install Terraform & Packer
```bash
cd /usr/local/bin
wget https://releases.hashicorp.com/terraform/1.10.3/terraform_1.10.3_linux_amd64.zip
unzip terraform_1.10.3_linux_amd64.zip

wget https://releases.hashicorp.com/packer/1.11.2/packer_1.11.2_linux_amd64.zip
unzip packer_1.11.2_linux_amd64.zip
```
- **Terraform** is used for Infrastructure as Code (IaC).
- **Packer** is used for VM image creation.

## 6. Install Ansible & Configure It
```bash
sudo apt update
sudo apt install -y ansible
```
- Installs Ansible for automation.

## 7. Install Trivy (Security Scanner)
```bash
cd /usr/local/bin
wget https://github.com/aquasecurity/trivy/releases/download/v0.41.0/trivy_0.41.0_Linux-64bit.deb
dpkg -i trivy_0.41.0_Linux-64bit.deb
```
- **Trivy** is used for container security scanning.

## 8. Install Jenkins
```bash
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc >/dev/null

echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list >/dev/null

sudo apt-get update
sudo apt-get install -y jenkins=2.426.2
```
- Installs Jenkins v2.426.2 for CI/CD.

## 9. Install Required Jenkins Plugins
✅ AWS Steps Plugin  
✅ Docker Plugin  
✅ SonarQube Scanner v2.15  
✅ Multibranch Scan Webhook Trigger  
✅ Trivy Plugin  
✅ Slack Notification Plugin  
✅ Ansible Plugin  

## 10. Configure SSL Certificates
```bash
snap install --classic certbot

certbot certonly --manual --preferred-challenges=dns --key-type rsa \
    --email admin@example.com --server https://acme-v02.api.letsencrypt.org/directory \
    --agree-tos -d "*.example.com"
```
- Generates SSL certificates for secure Jenkins access.

### Convert SSL Certificate to JKS Format
```bash
openssl pkcs12 -inkey privkey.pem -in cert.pem -export -out certificate.p12
keytool -importkeystore -srckeystore certificate.p12 -srcstoretype pkcs12 \
    -destkeystore jenkinsserver.jks -deststoretype JKS
```
- Converts SSL certificates into JKS format for Jenkins.

## 11. Configure Jenkins for HTTPS
```bash
sudo cp jenkinsserver.jks /var/lib/jenkins/
sudo chown jenkins:jenkins /var/lib/jenkins/jenkinsserver.jks

sudo nano /lib/systemd/system/jenkins.service
```
Add the following lines:
```bash
Environment="JENKINS_PORT=8080"
Environment="JENKINS_HTTPS_PORT=8443"
Environment="JENKINS_HTTPS_KEYSTORE=/var/lib/jenkins/jenkinsserver.jks"
Environment="JENKINS_HTTPS_KEYSTORE_PASSWORD=yourpassword"
```

## 12. Restart Jenkins & Grant Docker Access
```bash
sudo usermod -aG docker jenkins
sudo systemctl daemon-reload
sudo systemctl restart jenkins
sudo systemctl status jenkins
```
- Restarts Jenkins with HTTPS & Docker access.

---
🎉 **DevSecOps pipeline setup completed for Go and HTML applications!**

