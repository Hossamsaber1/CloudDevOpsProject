def call(String imageUri) {

    sh """
        docker rmi ${imageUri} || true
    """
}