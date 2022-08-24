#!/usr/bin/env groovy

def call() {
    echo "building the application..."
    sh 'pwd'
    sh 'ls'
    sh 'mvn clean package'   
}
