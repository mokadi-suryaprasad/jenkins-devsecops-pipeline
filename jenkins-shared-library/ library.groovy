// Jenkins Shared Library Entry Point
import jenkins.scm.api.SCMHead
import jenkins.scm.api.SCMSource
import jenkins.scm.api.trait.SCMSourceTrait

// Define the shared library configuration
class Library implements Serializable {
    def steps
    Library(steps) { this.steps = steps }
    
    def call() {
        steps.echo "Jenkins Shared Library Initialized"
    }
}
