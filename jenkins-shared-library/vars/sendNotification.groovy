def call(Map config = [:]) {
    script {
        def message = config.success ? 
            "${config.language} pipeline executed successfully: ${env.BUILD_URL}" :
            "${config.language} pipeline failed: ${env.BUILD_URL}"

        slackSend(color: config.success ? 'good' : 'danger', message: message)
    }
}
