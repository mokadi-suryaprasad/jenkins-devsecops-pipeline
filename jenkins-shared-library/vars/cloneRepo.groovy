def call() {
    script {
        withCredentials([string(credentialsId: 'github-pat', variable: 'GITHUB_TOKEN')]) {
            echo "Cloning repository from GitHub"
            sh '''
                rm -rf workspace
                git clone -b $GIT_BRANCH https://x-access-token:$GITHUB_TOKEN@github.com/$REPO_NAME.git workspace
                cd workspace
                git config --global user.email "msuryaprasad11@gmail.com"
                git config --global user.name "MSR"
            '''
        }
    }
}
