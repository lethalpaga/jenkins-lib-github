def getGitCommit() {
  sh returnStdout:true, script: "git rev-parse HEAD"
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

@NonCPS
def toJson(payload){
  new JSON(payload)
}

def sendPayload(url, payload) {
  echo "Sending payload to ${url}"
  writeJSON file: 'payload.json', json: toJson(json_payload)
  payload = null
  json_payload = null
  sh """
  set +x
  curl -f -X POST -u ${githubUser}:${githubPassword} ${url} -d @payload.json \
              -H 'X-GitHub-Media-Type: application/vnd.github.ant-man-preview+json' > response.json
  set -x
  """
  readJSON file: 'response.json'
}
  
def createDeployment(target, task = "deploy", description = "", required_contexts = []) {
  def rev = getGitCommit().trim()
  echo "Deploying rev ${rev} to ${target}"
  
  def payload = [
    ref: "${rev}",
    task: "${task}",
    environment: "${target}",
    description: "${description}",
    required_contexts: []
  ]
  def response = sendPayload("${githubUrl}/deployments", payload)
  echo "response is ${response['url'].inspect()}"
  return response['url']
}

// Create a new deployement status
// @param url Base url to the deployment as return by createDeployment)
// @param payload Status parameters map as per https://developer.github.com/v3/repos/deployments/#create-a-deployment-status     
def createDeploymentStatus(url, payload){
  def rev = getGitCommit()
  echo "Updating status of ${url}"
  
  def response = sendPayload("${url}/statuses", payload)
  return response['url']
}