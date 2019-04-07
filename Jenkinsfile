node {
   stage ('Checkout') {
  	checkout scm 
   }

   stage('Test') {
       // steps {
            sh 'pwd'
            sh 'chmod +x mvnw'
            sh "./mvnw clean test"
       // }
   }

   stage('Build') {
       // steps {
            sh "./mvnw package -DskipTests=true"
            step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
      //  }
   }

   post {
	always {
		junit 'target/surefire-reports/*.xml'
	}
   }
}
