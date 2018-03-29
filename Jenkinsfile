#!/usr/bin/env groovy

pipeline {
  agent any

  parameters {
    booleanParam(defaultValue: true, description: '', name: 'runEndToEndTestsOnPR')
    booleanParam(defaultValue: true, description: '', name: 'runAcceptTestsOnPR')
    booleanParam(defaultValue: false, description: '', name: 'runZapTestsOnPR')
    string(defaultValue: '', description: '', name: 'targetEnv')
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
    RUN_ACCEPT_ON_PR = "${params.runAcceptTestsOnPR}"
    RUN_ZAP_ON_PR = "${params.runZapTestsOnPR}"
    TARGET_ENV = "${params.targetEnv}"
  }

  stages {
    stage('git submodule update') {
      steps {
        sh 'git submodule update --init --recursive'
      }
    }
    stage('Maven Build') {
      when { environment name: 'TARGET_ENV', value: 'test' }
      steps {
        script {
          def long stepBuildTime = System.currentTimeMillis()
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
        when { environment name: 'TARGET_ENV', value: 'test' }

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
      when { environment name: 'TARGET_ENV', value: 'test' }
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
        stage('Accept Tests') {
            when {
                anyOf {
                  branch 'master'
                  environment name: 'RUN_ACCEPT_ON_PR', value: 'true'
                }
            }
            steps {
                runAccept("cardid")
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
      when { environment name: 'TARGET_ENV', value: 'test' }
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
        anyOf {
          environment name: 'TARGET_ENV', value: 'test'
          branch 'master'
        }
      }
      steps {
        deployEcs("cardid")
      }
    }
    stage('Smoke Tests') {
      when { environment name: 'TARGET_ENV', value: 'test' }
      failFast true
      parallel {
        stage('Card Payment Smoke Test') {
          when { branch 'master' }
          steps { runCardSmokeTest() }
        }
        stage('Product Smoke Test') {
          when { branch 'master' }
          steps { runProductsSmokeTest() }
        }
      }
    }
    stage('Complete') {
      when { environment name: 'TARGET_ENV', value: 'test' }
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
    stage('Staging step') {
      when { environment name: 'TARGET_ENV', value: 'staging' }
      steps {
        script {'echo hello'}
      }
    }
  }
}
