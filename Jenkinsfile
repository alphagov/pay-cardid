#!/usr/bin/env groovy

pipeline {
    agent any
    options {
        ansiColor('xterm')
        timestamps()
    }
    stages {
       stage('Git Submodule Update') {
            steps {
                sh 'git submodule update --init --recursive'
            }
        }
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
                runEndToEnd("cardid")
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                deploy("cardid", "test")
            }
        }
    }
}
