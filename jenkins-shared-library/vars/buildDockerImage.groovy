def call(Map config = [:]) {
    script {
        dir('workspace') {
            def dockerfilePath = config.language == 'go' ? 'backend/Dockerfile' : 'frontend/Dockerfile'
            echo "Building Docker image: $IMAGE_NAME"
            sh "docker build -t $IMAGE_NAME -f $dockerfilePath . || exit 1"
        }
    }
}
