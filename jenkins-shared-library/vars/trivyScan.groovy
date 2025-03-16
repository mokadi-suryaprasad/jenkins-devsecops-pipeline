def call(Map config = [:]) {
    script {
        echo "Running Trivy vulnerability scan on $IMAGE_NAME"
        sh '''
            trivy image --exit-code 1 --severity CRITICAL,HIGH $IMAGE_NAME || {
                echo "Critical vulnerabilities found!";
                exit 1;
            }
        '''
    }
}
