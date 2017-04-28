#!/usr/bin/env groovy

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
        deploy("cardid", "test", null, true)
      }
    }
  }
}
