@Library('jenkins-shared-library') _

parallel(
    GoPipeline: {
        ciPipeline(language: 'go')
    },
    HTML: {
        ciPipeline(language: 'html')
    }
)
