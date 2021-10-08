pipeline {
    agent any

    tools {
        maven 'Maven'
        nodejs 'NodeJs'
    }
  
    stages {
        stage('initial'){
            steps{
             sh '''
              echo "PATH = ${PATH}"
              echo "M2_HOME = ${M2_HOME}"
              '''
            }
        }
        
        stage('Compile'){
            steps{
                sh 'mvn clean compile -e'
            }
        }
        
        stage('Test'){
            steps{
                sh 'mvn clean test -e'
            }
        }
        
        stage('SCA'){
            steps{
                sh 'mvn org.owasp:dependency-check-maven:check'
                
                archiveArtifacts artifacts: 'target/dependency-check-report.html', followSymlinks: false
            }
        }
        
        stage('Sonarqube'){
            steps{
                script{
                    def scannerHome = tool 'SonarQube Scanner'
                    
                    withSonarQubeEnv('Sonar Server'){
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ms-maven -Dsonar.sources=. -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/*/test/**/*, **/*/acceptance-test/**/*, **/*.html'"
                    }
                }
            }
        }
        
    }
    
           
}
