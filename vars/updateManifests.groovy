def call(String imageUri) {
    sh """
        sed -i 's|image: .*|image: ${imageUri}|g' k8s/base/deployment.yaml
    """
}