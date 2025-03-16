def call(Map config = [:]) {
    script {
        dir('workspace') {
            if (config.language == 'go') {
                echo "Running Go tests"
                sh 'go test ./... || exit 1'
            } else {
                echo "Skipping tests for HTML as it is static"
            }
        }
    }
}
