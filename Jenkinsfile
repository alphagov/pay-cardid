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
    lib("pay-jenkins-library@master")
  }

  environment {
    DOCKER_HOST = "unix:///var/run/docker.sock"
    RUN_END_TO_END_ON_PR = "${params.runEndToEndTestsOnPR}"
    RUN_ZAP_ON_PR = "${params.runZapTestsOnPR}"
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
          long stepBuildTime = System.currentTimeMillis()
 
          sh 'mvn clean package'
          postSuccessfulMetrics("cardid.maven-build", stepBuildTime)
        }
      }
      post {
        failure {
          postMetric("cardid.maven-build.failure", 1)
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
          postMetric("cardid.docker-build.failure", 1)
        }
      }
    }
    stage('Tests') {
      failFast true
      parallel {
        stage('Card Payment End-to-End Tests') {
            when {
                anyOf {
                  branch 'master'
                  environment name: 'RUN_END_TO_END_ON_PR', value: 'true'
                }
            }
            steps {
                runCardPaymentsE2E("cardid")
            }
        }
         stage('ZAP Tests') {
            when {
                anyOf {
                  branch 'master'
                  environment name: 'RUN_ZAP_ON_PR', value: 'true'
                }
            }
            steps {
                runZap("cardid")
            }
         }
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
          postMetric("cardid.docker-tag.failure", 1)
        }
      }
    }
    stage('Deploy') {
      when {
        branch 'master'
      }
      steps {
        deployEcs("cardid")
      }
    }
    stage('Card Payment Smoke Test') {
      when { branch 'master' }
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
