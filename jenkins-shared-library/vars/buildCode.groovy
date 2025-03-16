def call(Map config = [:]) {
    script {
        dir('workspace') {
            if (config.language == 'go') {
                echo "Building Go application"
                sh 'go build -o app || exit 1'
            } else {
                echo "Skipping build step for HTML"
            }
        }
    }
}
