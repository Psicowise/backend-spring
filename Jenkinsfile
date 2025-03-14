pipeline {
    agent any

    environment {
        GITHUB_CREDENTIALS_ID = 'github-credentials' // Substitua pelo ID correto das credenciais no Jenkins
    }

    stages {
        stage('Checkout do Código') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Psicowise/backend-spring.git',
                    credentialsId: "${GITHUB_CREDENTIALS_ID}"
            }
        }

        stage('Permissão para mvnw') {
            steps {
                sh 'chmod +x mvnw'
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
        }

        stage('Testes') {
            steps {
                sh './mvnw test'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                // Adicione comandos específicos para deploy, por exemplo:
                // sh 'scp target/*.jar usuario@servidor:/caminho/do/deploy/'
            }
        }
    }

    post {
        success {
            echo 'Pipeline concluído com sucesso!'
        }
        failure {
            echo 'Pipeline falhou!'
        }
    }
}