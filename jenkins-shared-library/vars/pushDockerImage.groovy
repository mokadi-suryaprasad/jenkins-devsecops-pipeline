def call(Map config = [:]) {
    script {
        echo "Logging into Docker Hub and pushing the image"
        withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
            sh '''
                echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                docker push $IMAGE_NAME || exit 1
            '''
        }
    }
}
