pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        OPENSHIFT_API = 'https://api.rm1.0a51.p1.openshiftapps.com:6443'
        OPENSHIFT_PROJECT = 'sureshkumarpeter-dev'

        BACKEND_IMAGE = 'ghcr.io/sureshkthuthika/investtrack-backend'
        FRONTEND_IMAGE = 'ghcr.io/sureshkthuthika/investtrack-frontend'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    env.BACKEND_TAG = "${BUILD_NUMBER}"
                }

                sh '''
                    docker build \
                      -t ${BACKEND_IMAGE}:${BACKEND_TAG} \
                      -f backend/investtrack-backend/Dockerfile \
                      backend/investtrack-backend
                '''
            }
        }

        stage('Build Frontend') {
            steps {
                script {
                    env.FRONTEND_TAG = "${BUILD_NUMBER}"
                }

                sh '''
                    docker build \
                      -t ${FRONTEND_IMAGE}:${FRONTEND_TAG} \
                      -f frontend/Dockerfile \
                      frontend
                '''
            }
        }

        stage('Push Images to GHCR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'ghcr-credentials',
                        usernameVariable: 'GHCR_USER',
                        passwordVariable: 'GHCR_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x

                        echo "$GHCR_TOKEN" | docker login ghcr.io \
                          -u "$GHCR_USER" --password-stdin

                        docker push ${BACKEND_IMAGE}:${BACKEND_TAG}
                        docker push ${FRONTEND_IMAGE}:${FRONTEND_TAG}

                        docker logout ghcr.io
                    '''
                }
            }
        }

        stage('Deploy to OpenShift') {
            steps {
                withCredentials([
                    string(credentialsId: 'openshift-token',
                           variable: 'OPENSHIFT_TOKEN'),
                    string(credentialsId: 'mongodb-uri',
                           variable: 'MONGODB_URI'),
                    string(credentialsId: 'jwt-secret',
                           variable: 'JWT_SECRET'),
                    usernamePassword(
                        credentialsId: 'ghcr-credentials',
                        usernameVariable: 'GHCR_USER',
                        passwordVariable: 'GHCR_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x

                        # Login to OpenShift
                        oc login "$OPENSHIFT_API" \
                          --token="$OPENSHIFT_TOKEN"

                        oc project "$OPENSHIFT_PROJECT"

                        # Create/update application secrets
                        oc create secret generic investtrack-secrets \
                          --from-literal=MONGODB_URI="$MONGODB_URI" \
                          --from-literal=JWT_SECRET="$JWT_SECRET" \
                          --dry-run=client -o yaml | oc apply -f -

                        # Configure private GHCR image access
                        oc create secret docker-registry ghcr-pull-secret \
                          --docker-server=ghcr.io \
                          --docker-username="$GHCR_USER" \
                          --docker-password="$GHCR_TOKEN" \
                          --dry-run=client -o yaml | oc apply -f -

                        oc secrets link default ghcr-pull-secret --for=pull

                        # Deploy backend
                        oc create deployment investtrack-backend \
                          --image=${BACKEND_IMAGE}:${BACKEND_TAG} \
                          --dry-run=client -o yaml | oc apply -f -

                        oc set env deployment/investtrack-backend \
                          --from=secret/investtrack-secrets

                        oc set image deployment/investtrack-backend \
                          investtrack-backend=${BACKEND_IMAGE}:${BACKEND_TAG}

                        # Create backend service if needed
                        oc expose deployment investtrack-backend \
                          --port=8080 --target-port=8080 \
                          --dry-run=client -o yaml | oc apply -f -

                        # Deploy frontend
                        oc create deployment investtrack-frontend \
                          --image=${FRONTEND_IMAGE}:${FRONTEND_TAG} \
                          --dry-run=client -o yaml | oc apply -f -

                        oc set image deployment/investtrack-frontend \
                          investtrack-frontend=${FRONTEND_IMAGE}:${FRONTEND_TAG}

                        # Create frontend service
                        oc expose deployment investtrack-frontend --port=80 --target-port=8080 --dry-run=client -o yaml | oc apply -f -

                        # Create public frontend route
                        oc expose service investtrack-frontend \
                          --dry-run=client -o yaml | oc apply -f -

                        # Wait for deployments
                        oc rollout status deployment/investtrack-backend \
                          --timeout=180s

                        oc rollout status deployment/investtrack-frontend \
                          --timeout=180s

                        # Display deployment information
                        oc get pods
                        oc get services
                        oc get routes
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'InvestTrack CI/CD pipeline completed successfully!'
        }

        failure {
            echo 'InvestTrack CI/CD pipeline failed. Check the stage logs.'
        }
    }
}