def call(String imageUri, String awsRegion) {

    sh """
        aws ecr get-login-password --region ${awsRegion} | \
        docker login --username AWS --password-stdin ${imageUri.split('/')[0]}

        docker push ${imageUri}
    """
}