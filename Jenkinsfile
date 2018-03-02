#!/usr/bin/env groovy

withCredentials([
    string(credentialsId: 'graphite_api_key', variable: 'HOSTED_GRAPHITE_API_KEY'),
    string(credentialsId: 'graphite_account_id', variable: 'HOSTED_GRAPHITE_ACCOUNT_ID')]
) {
    pipeline {
      agent any

      options {
        ansiColor('xterm')
        timestamps()
      }

      libraries {
        lib("pay-jenkins-library@master")
      }

      environment {
        DOCKER_HOST = "unix:///var/run/docker.sock"
      }

      stages {
        stage('git submodule update') {
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
                app = "cardid"
              }
            }
          }
        }
        stage('Test') {
          steps {
            runEndToEnd("cardid")
          }
        }
        stage('Docker Tag') {
          steps {
            script {
              dockerTag {
                app = "cardid"
              }
            }
          }
        }
        stage('Deploy') {
          when {
            branch 'master'
          }
          steps {
            deployEcs("cardid", "test", null, true, true)
          }
        }
      }
    }
}
