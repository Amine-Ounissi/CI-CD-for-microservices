pipeline {
    agent any
        parameters {
            choice(name:'MS', choices: [
                'registry',
                'edge',
                'token-converter'
            ])
            string(name:'COMMAND',defaultValue: '', description:'Version of backbase')
           }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'Intern', url: 'https://gitlab.com/infra-assurance/${MS}.git', credentialsId: 'gitlab'
            }
        }
        stage('run CLI') {
            steps {
                sh 'kubectl ${COMMAND} --namespace=intern-deploy'
            }
        }
        stage('Clean Workspace'){
            steps{
                script{
                    cleanWs()
                }
            }
        }
    }
}
