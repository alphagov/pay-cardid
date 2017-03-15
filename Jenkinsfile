#!/usr/bin/env groovy

pipeline {
    agent any
    options {
        ansiColor('xterm')
        timestamps()
    }
    stages {
       stage('Maven Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    buildApp{
                        app = 'cardid'
                    }
                }
            }
        }
        stage('Test') {
            steps {
                runEndToEnd("cardid", "${env.REQ_COMMIT_ID}-${env.BUILD_NUMBER}")
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
                echo 'I like badgers'
            }
        }
    }
}
