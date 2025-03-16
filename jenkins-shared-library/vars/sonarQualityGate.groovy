def call() {
    script {
        def status = sh(
            script: """
            curl -s -u $SONAR_TOKEN: "$SONAR_HOST_URL/api/qualitygates/project_status?projectKey=$SONAR_PROJECT_KEY" | jq -r '.projectStatus.status'
            """, returnStdout: true
        ).trim()

        if (status != "OK") {
            error "SonarQube Quality Gate Failed!"
        }
    }
}
