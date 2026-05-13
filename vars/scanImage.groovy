def call(String imageUri) {
    sh """
        trivy image --exit-code 0 --severity HIGH,CRITICAL ${imageUri}
    """
}