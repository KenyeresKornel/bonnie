pipeline {
    agent any

    environment {
       DB_ROOT_USERNAME = credentials('DB_ROOT_USERNAME')
       DB_ROOT_PWD = credentials('DB_ROOT_PWD')
       DB_USERNAME = credentials('DB_USERNAME')
       JASYPT_DB_DECRYPT_PWD = credentials('JASYPT_DB_DECRYPT_PWD')
       JASYPT_DB_CONNECTION_PWD = credentials('JASYPT_DB_CONNECTION_PWD')
    }
    stages {

        stage('Build backend') {
            agent {
                docker {
                    image 'maven:3.8.6-openjdk-18'
                    args '-v /root/.m2:/root/.m2 -v /var/lib/jenkins/workspace:/out'
                }
            }
            steps {
                sh '''
                      mvn -B -DskipTests -DskipCopy=true clean package install
                      mvn -f starter/pom.xml package spring-boot:repackage
                      cp starter/target/starter-1.0-SNAPSHOT.jar /out/
                      cp bonnie-rest/src/helper/resources/encoder.ts frontend/generated-client/encoder.ts
                      cp -r frontend/generated-client /out/'''
            }
        }

        stage('Create backend Docker image') {
            steps {
                sh '''mv ../starter-1.0-SNAPSHOT.jar .
                      docker build -t bonnie-backend:latest .'''
            }
        }

        stage('Build frontend') {
            agent {
                docker {
                    image 'node:16.18.1-alpine'
                    args '-v $PWD/frontend:/frontend -v /var/lib/jenkins/workspace:/out'
                }
            }
            steps {
                sh '''
                  cd frontend
                  rm -rf node_modules
                  mkdir -p /out/.npm
                  npm install '--cache=/out/.npm'
                  npm run build '--cache=/out/.npm' -- --c production
                  cd ..
                  cp -r ./frontend /out/
                  rm -rf /out/frontend/generated-client
                  mv /out/generated-client /out/frontend/'''
            }
        }

        stage('Create frontend Docker image') {
            steps {
                sh '''
                    rm -rf ./frontend
                    mv ../frontend .
                    docker build -f Dockerfile-frontend -t bonnie-frontend:latest .'''
            }
        }

        stage('Restart docker-compose environment') {
            steps {
                sh '''
                    docker-compose down
                    docker-compose up -d'''
            }
        }

    }
}
