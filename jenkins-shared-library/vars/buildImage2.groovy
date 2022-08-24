#!/usr/bin/env groovy

def call() {
    echo "building the docker image..."
    //withCredentials([usernamePassword(credentialsId: 'nexus-docker-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t 10.1.0.4:5000/repository/value/infra-amine-intern/registry:latest1 .'
        //sh "echo intern | docker login -u intern --password-stdin 10.1.0.4:5000"
        sh 'docker push 10.1.0.4:5000/repository/value/infra-amine-intern/registry:latest1'
    //}
}
