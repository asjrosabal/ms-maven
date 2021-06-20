pipeline {
    agent any

    tools {
        maven 'Maven'
        nodejs "NodeJs"
    }
    stages {
      stage ('Initial') {
            steps {
              sh '''
                   echo "PATH = ${PATH}"
                   echo "M2_HOME = ${M2_HOME}"
               '''
            }
        }
        stage ('Compile') {
            steps {
                 sh 'mvn clean compile -e'
            }
        }
        stage ('Test') {
            steps {
                 sh 'mvn clean test -e'
            }
        }

        stage('SonarQube analysis') {
           steps{
                script {
                    def scannerHome = tool 'SonarQube Scanner';//def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    withSonarQubeEnv('Sonar Server') {
                      sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=Ms-Maven -Dsonar.sources=target/ -Dsonar.host.url=http://172.18.0.3:9000 -Dsonar.login=f74fc670543b5f3d217066fcdd8340ec592be0cd"
                      //sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=Maven -Dsonar.host.url=http://172.18.0.3:9000 -Dsonar.login=f74fc670543b5f3d217066fcdd8340ec592be0cd -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json -Dsonar.dependencyCheck.xmlReportPath=target/dependency-check-report.xml -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html"

                    }
                }
           }
        }

        stage ('SCA') {
            steps {
                 sh 'mvn org.owasp:dependency-check-maven:check'
                dependencyCheckPublisher failedNewCritical: 5, failedTotalCritical: 10, pattern: 'terget/dad.xml', unstableNewCritical: 3, unstableTotalCritical: 5
            }
        }
        
          stage('ZAP'){
			steps{
				sh("echo ${env.WORKSPACE}; ls -l;")
				//checkoutGitSCM("master","https://your_git_repo")
				sh("bash -c \"chmod +x ${env.WORKSPACE}/*.sh\"")
				//sh("${env.WORKSPACE}/validate_input.sh")
				sh("${env.WORKSPACE}/runZapScan.sh ${params.ZAP_TARGET_URL} ${env.WORKSPACE} ${params.ZAP_ALERT_LVL}")
			}
		}

    }
}
