pipeline {
    agent {
    	docker {
    		image 'openjdk:8-alpine'
    	}
    }
    
   stages{
        stage ('Checkout') {
            steps{
      	        git branch: 'docker',
                url: 'https://github.com/davidkey/roster.git'
            }
       }
    
       stage('Test') {
            steps {
                sh 'pwd'
                sh 'chmod +x mvnw'
                sh "./mvnw clean test"
    		junit 'target/surefire-reports/*.xml'
            }
       }
    
       stage('Build') {
            steps {
                sh "./mvnw clean package -DskipTests=true"
                step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
            }
       }
   }
}
