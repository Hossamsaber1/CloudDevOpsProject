def call(String imageUri) {

    sh """
        docker build -t ${imageUri} ./app
    """
}