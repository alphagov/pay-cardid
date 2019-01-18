#!/usr/bin/env groovy

pipeline {
  agent any

  parameters {
    booleanParam(defaultValue: true, description: '', name: 'runEndToEndTestsOnPR')
    booleanParam(defaultValue: false, description: '', name: 'runZapTestsOnPR')
  }

  options {
    ansiColor('xterm')
    timestamps()
  }

  libraries {
    lib("pay-jenkins-library@smoke_test_check")
  }

  environment {
    DOCKER_HOST = "unix:///var/run/docker.sock"
    RUN_END_TO_END_ON_PR = "${params.runEndToEndTestsOnPR}"
    RUN_ZAP_ON_PR = "${params.runZapTestsOnPR}"
    GIT_BRANCH="PP-4440-stripe-smoke-tests"
  }

  stages {
    stage('Card Payment Smoke Test') {
      steps { runCardSmokeTest() }
    }
    stage('Complete') {
      failFast true
      parallel {
        stage('Tag Build') {
          when {
            branch 'master'
          }
          steps {
            tagDeployment("cardid")
          }
        }
        stage('Trigger Deploy Notification') {
          when {
            branch 'master'
          }
          steps {
            triggerGraphiteDeployEvent("cardid")
          }
        }
      }
    }
  }
  post {
    failure {
      postMetric(appendBranchSuffix("cardid") + ".failure", 1)
    }
    success {
      postSuccessfulMetrics(appendBranchSuffix("cardid"))
    }
  }
}
