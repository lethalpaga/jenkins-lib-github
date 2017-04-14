# jenkins-lib-github
Jenkins Pipeline library for the github API

## Features
* Create Deployments and statuses:
```groovy
// Setup your Github instance
github.setUrl('https://api.github.com/repos/<user>/<repo>')
github.setUser(env.GITHUB_USER)
github.setPassword(env.GITHUB_PASSWD)

// Create a deployment
def deployment_url = github.createDeployment('prod')

// Send status updates
github.createDeploymentStatus(deployment_url,
                              "pending",
                              "https://my-jenkins/job/myjob",
                              "Deployment started")

github.createDeploymentStatus(deployment_url,
                              "success",
                              "https://my-jenkins/job/myjob",
                              "Deployment successful!")
```
