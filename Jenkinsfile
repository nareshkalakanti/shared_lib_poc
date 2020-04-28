import groovy.transform.Field
import hudson.FilePath
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

@Library('Shared_Libary') _

env.MAVEN = "mvn --batch-mode"

pipeline {
    agent none

    options{
        timeout(time: 1, unit: 'HOURS')
    }

    stages {
        stage('Checkout'){
            steps{
                doCheckout()
                stash includes: '**', name: 'project'
            }
        }
        stage('Static Analysis') {
            steps {
                doStaticAnalysis()
            }
        }
        stage('Unit Tests') {
            steps {
                doUnitTests()
            }
        }
        stage('Integration Tests') {
            steps {
                doIntegrationTests()
            }
        }
        stage('Acceptance Tests') {
            agent {
                label 'docker-in-docker'
            }

            steps {
                unstash 'project'
                doAcceptanceTests()
                stash includes: "target/**", name: 'acceptance-tests', allowEmpty: true
            }
        }
        stage('Test Coverage') {
            steps {
                unstash 'acceptance-tests'
                doTestCoverage()                
            }
        }
        stage('Build Report') {
            steps {
                echo 'Building test reports...'
                doBuildReport(env.WORKSPACE)
                publishSnapshotsTests()
                script {
                    env.generateReportOnFailure = false
                }
            }
        }
        stage('Sonar') {
            steps {
                doSonar()
            }
        }
        stage('Release to Artifactory') {
            when {
			    expression { "${env.BRANCH_NAME}" == 'master' }
            }
			steps {
				publishReleaseTests()
                doReleaseToArtifactory()
                script{
                    env.buildType = "release"
                }                                
			}
			post {
				failure {
					script {
						doArtifactoryRollback()
					}
				}
			}
        }
   }
   post {
       failure {
           script{           
                utils.sendEmail()
                doBuildReportPostAction(env.WORKSPACE)
                publishSnapshotsTests()
                currentBuild.result = "FAILURE"
                publishMetrics(testCount)
            }               
       }

       success {
           script{
                currentBuild.result = "SUCCESS"
                publishMetrics(testCount)
           }
       }

       always{
           script{
                testCount = getTestCount()
           }
           deleteOldBuilds(5)
       }
   }
} 