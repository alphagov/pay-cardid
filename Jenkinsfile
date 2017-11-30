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
  }
}
