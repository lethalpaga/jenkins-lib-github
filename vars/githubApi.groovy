import groovy.json.JsonOutput

def getGitCommit() {
  sh(returnStdout:true, script: "git rev-parse HEAD").trim()
}

def setUrl(url) {
  githubUrl = url
}

def setUser(user) {
  githubUser = user
}

def setPassword(passwd) {
  githubPassword = passwd
}

def setOrg(org) {
  githubOrg = org
}

def setRepo(repo) {
  githubRepo = repo
}

// Sends an HTTP query to github
def sendPayload(url, payload) {
  writeFile file: 'payload.json', text: groovy.json.JsonOutput.toJson(payload)
  sh """
  set +x
  curl -f -X POST -u '${githubUser}:${githubPassword}' '${url}' -d @payload.json \
              -H 'X-GitHub-Media-Type: application/vnd.github.ant-man-preview+json' > response.json
  set -x
  """
  readJSON file: 'response.json'
}

// Returns the Repos base endpoint
private githubApiRepoUrl() {
  "${githubUrl}/repos/${githubOrg}/${githubRepo}"
}
  
// Create a new deployement
// @params Hash containing the parameter as per https://developer.github.com/v3/repos/deployments/#create-a-deployment
def createDeployment(Map payload) {
  def response = sendPayload("${githubApiRepoUrl()}/deployments", payload)
  echo "response is ${response['url'].inspect()}"
  return response['url']
}

// Send a new deployement status
// @param url Base url to the deployment as return by createDeployment)
// @param payload Status parameters map as per https://developer.github.com/v3/repos/deployments/#create-a-deployment-status     
def createDeploymentStatus(url, Map payload){
  def rev = getGitCommit()
  echo "Updating status of ${url} with ${payload}"
  
  def response = sendPayload("${url}/statuses", payload)
  return response['url']
}

