def call() {

    sh """
        git config user.email "jenkins@clouddevops.local"
        git config user.name "Jenkins CI"

        git remote set-url origin \
        https://${GITHUB_USERNAME}:${GITHUB_TOKEN}@github.com/Hossamsaber1/CloudDevOpsProject.git

        git add k8s/base/deployment.yaml

        git commit -m "Update Kubernetes image tag to build ${BUILD_NUMBER}" \
        || echo "No changes to commit"

        git push origin dev
    """
}