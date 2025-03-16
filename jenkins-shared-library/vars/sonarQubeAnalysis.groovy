def call(Map config = [:]) {
    script {
        dir('workspace') {
            def sonarFile = config.language == 'go' ? 'resources/sonar-project-go.properties' : 'resources/sonar-project-html.properties'
            echo "Running SonarQube Analysis"
            sh '''
                sonar-scanner -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                              -Dproject.settings=$sonarFile \
                              -Dsonar.login=$SONAR_TOKEN || exit 1
            '''
        }
    }
}
