def buildJar() {
  echo "building the application..."
  sh'mvn clean package'
}

def buildImage() {
  echo "building the docker image..."
  withCredentials([usernamePassword(credentialsId: 'nexus-docker-repo', passwordVariable: 'PASS', usernameVariable:'USER')]) {
      sh 'docker build -t 40.87.151.190:8081/repository/value/infra-intern:$IMAGE_NAME .'
      sh "echo $PASS | docker login -u $USER --password-stdin 40.87.151.190:8081"
      sh 'docker push 40.87.151.190:8081/repository/value/infra-intern:$IMAGE_NAME'           
   }   
}


def deployApp() {
  echo 'deploying the application...'
}
