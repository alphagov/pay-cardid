#!/usr/bin/env groovy

pipeline {
  agent any

  parameters {
    booleanParam(defaultValue: false, description: '', name: 'runEndToEndTestsOnPR')
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
    JAVA_HOME="/usr/lib/jvm/java-1.11.0-openjdk-amd64"
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

          sh 'mvn -version'
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
      stages {
        stage('End-to-End Tests') {
            when {
                anyOf {
                  branch 'master'
                  environment name: 'RUN_END_TO_END_ON_PR', value: 'true'
                }
            }
            steps {
                runAppE2E("cardid", "card")
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
    always {
      junit "**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml"
    }
  }
}
