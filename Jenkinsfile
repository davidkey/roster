node {
   stage ('Checkout') {
  	checkout scm 
   }

   stage('Test') {
       // steps {
            sh 'pwd'
            sh 'chmod +x mvnw'
            sh "./mvnw clean test"
	    junit 'target/surefire-reports/*.xml'
       // }
   }

   stage('Build') {
       // steps {
            sh "./mvnw clean package -DskipTests=true"
            step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
      //  }
   }
}
