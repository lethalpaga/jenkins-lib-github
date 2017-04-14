@Library('github@test') _

pipeline {
  agent any
  stages {
    stage("Create deployment status") {
      steps {
        script {
          sh 'env'

          script {
            //library('github') 
            withCredentials([usernamePassword(credentialsId: 'github-token', passwordVariable: 'GITHUB_PASSWD', usernameVariable: 'GITHUB_USER')]) {
              github.setUrl('https://api.github.com/repos/lethalpaga/jenkins-lib-github')
              github.setUser(env.GITHUB_USER)
              github.setPassword(env.GITHUB_PASSWD)
              def deployment_url = github.createDeployment('prod')
              echo "Deployment url = ${deployment_url}"
              github.createDeploymentStatus(deployment_url,
                                            "pending",
                                            "http://localhost:8080",
                                            "Deployment started")
                                            
              github.createDeploymentStatus(deployment_url,
                                            "success",
                                            "http://localhost:8080",
                                            "Deployment started")
            }
          }
        }
      }
    }
  }
}