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
    HOSTED_GRAPHITE_ACCOUNT_ID = credentials('graphite_account_id')
    HOSTED_GRAPHITE_API_KEY = credentials('graphite_api_key')
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
      post {
        failure {
          postMetric("cardid.maven-build.failure", 1, "new")
        }
        success {
          postSuccessfulMetrics("cardid.maven-build")
        }
      }
    }
    stage('Docker Build') {
      steps {
        script {
          buildAppWithMetrics{
            app = "cardid"
          }
        }
      }
      post {
        failure {
          postMetric("cardid.docker-build.failure", 1, "new")
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
          dockerTagWithMetrics {
            app = "cardid"
          }
        }
      }
      post {
        failure {
          postMetric("cardid.docker-tag.failure", 1, "new")
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
  post {
    failure {
      postMetric("cardid.failure", 1, "new")
    }
    success {
      postSuccessfulMetrics("cardid")
    }
  }
}
