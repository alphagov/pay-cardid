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
        script {
          def long stepBuildTime = System.currentTimeMillis()
 
          sh 'mvn clean package'
          postSuccessfulMetrics("cardid.maven-build", stepBuildTime)
        }
      }
      post {
        failure {
          postMetric("cardid.maven-build.failure", 1, "new")
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
