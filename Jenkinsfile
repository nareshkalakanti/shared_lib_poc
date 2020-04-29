pipeline {
    agent none

    options{
        timeout(time: 1, unit: 'HOURS')
    }

    stages {
        
        stage('Checkout') {
            steps {
                checkout(
                    [$class: 'GitSCM',
                     branches: [[name: '*/master']],
                     doGenerateSubmoduleConfigurations: false,
                     extensions: [],
                     submoduleCfg: [],
                     userRemoteConfigs: [[credentialsId: '',
                   url: '']
                ]])
            }
        }
        stage('Build') {
            steps {
                sh '''
                mvn clean –U install -Dmaven.test.skip=true
                '''
            }
        }
        stage('Tests') {
            steps {
                sh '''
                  mvn clean –U test
                '''
            }
        }
        stage('SonarQube') {
            steps {
               when {
                // Currently Sonar is not run for pull requests
                expression { env.CHANGE_ID == null }
            }
            environment {
                SONAR_TOKEN = credentials('sonarqube-token')
            }
            steps {
                withMaven(maven: 'maven-latest', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS} ${LINUX_MVN_RANDOM}') {
                 //Todo: sonarqube properies file
                 sh ''' 
                     mvn clean –U org.jacoco:jacoco-maven-plugin:0.7.4.201502262128:prepare-agent install
                     org.jacoco:jacoco-maven-plugin:0.7.4.201502262128:report
                     org.jacoco:jacoco-maven-plugin:0.7.4.201502262128:merge
                     -Djacoco.destFile=${WORKSPACE}/target-jacoco/merged.exec
                     -Dmaven.test.failure.ignore=true -Djacoco.append=true
                 '''
                }
            }        
            }
        }
        stage('Archive') {
            steps {
                script {
                    step([$class     : 'Artifact',
                          projectName: 'Test-BUILD',
                          filter     : "**/*",
                          target     : '.']);
                }
            }
        }
           stage('Publish') {
            steps {
                echo 'Build the artifacts..'
                //Todo: artifactory configuration  not clear so adding a script after build
                sh '''
                mvn clean -U install --P jboss-7.1 -Dmaven.test.skip=true
                '''
                echo 'Publish the artifacts..'
                    script
                        {
                        def server = Artifactory.newServer('http://localhost:8081/artifactory', 'admin', 'test@123')
                        def server = Artifactory.server 'Artifac_dev_server1'
                        server.bypassProxy = true
                        server.upload(uploadSpec)
                        echo 'Uploaded the file to Jfrog Artifactory successfully'
                        }
            }
        }
         stage('Notify') {
            steps {
                echo 'Mail Notification...'
                mail body: 'Project build successful for job named testpipeline',
                from: 'test1@gmail.com',
                subject: 'project build successful',
                to: 'test2@gmail.com'
            }
        }
   }
} 