def call(Map config = [:]) {
    script {
        withCredentials([string(credentialsId: 'github-pat', variable: 'GITHUB_TOKEN')]) {
            dir('workspace') {
                def deploymentFile = config.language == 'go' ? 'kubernetes/backend-deployment.yaml' : 'kubernetes/frontend-deployment.yaml'
                echo "Updating Kubernetes deployment.yaml with new image"
                sh '''
                    yq eval '.spec.template.spec.containers[0].image = "$IMAGE_NAME"' -i $deploymentFile
                    git add $deploymentFile
                    git commit -m "Update deployment.yaml with image $IMAGE_NAME" || echo "No changes to commit"
                    git push https://x-access-token:$GITHUB_TOKEN@github.com/$REPO_NAME.git $GIT_BRANCH || echo "Git push failed. Check permissions."
                '''
            }
        }
    }
}
