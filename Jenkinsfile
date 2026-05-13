@Library('cloud-devops-shared-lib') _

pipeline {
    agent any

    environment {
        APP_NAME = 'ivolve-app'
        AWS_REGION = 'eu-north-1'
        ECR_REPO = 'clouddevopsproject-repo
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Prepare Environment') {
            steps {
                withCredentials([
                    string(credentialsId: 'AWS_ACCOUNT_ID', variable: 'AWS_ACCOUNT_ID')
                ]) {
                    script {
                        env.IMAGE_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"
                    }
                }
            }
        }

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
                withCredentials([
                    usernamePassword(
                        credentialsId: 'AWS_CREDENTIALS',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
                    pushImage("${IMAGE_URI}", "${AWS_REGION}")
                }
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
                withCredentials([
                    usernamePassword(
                        credentialsId: 'GITHUB_CREDENTIALS',
                        usernameVariable: 'GITHUB_USERNAME',
                        passwordVariable: 'GITHUB_TOKEN'
                    )
                ]) {
                    pushManifests()
                }
            }
        }
    }
}