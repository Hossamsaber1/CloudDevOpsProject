@Library('cloud-devops-shared-lib') _

pipeline {
    agent any

    environment {
        APP_NAME = 'ivolve-app'
        AWS_REGION = 'eu-north-1'
        ECR_REPO = 'clouddevopsproject'
        IMAGE_TAG = "${BUILD_NUMBER}"
        IMAGE_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"
    }

    stages {
        stage('Build Image') {
            steps {
                buildImage("${IMAGE_URI}")
            }
        }

        stage('Scan Image') {
            steps {
                scanImage("${IMAGE_URI}")
            }
        }

        stage('Push Image') {
            steps {
                pushImage("${IMAGE_URI}", "${AWS_REGION}")
            }
        }

        stage('Delete Image Locally') {
            steps {
                deleteImage("${IMAGE_URI}")
            }
        }

        stage('Update Manifests') {
            steps {
                updateManifests("${IMAGE_URI}")
            }
        }

        stage('Push Manifests') {
            steps {
                pushManifests()
            }
        }
    }
}