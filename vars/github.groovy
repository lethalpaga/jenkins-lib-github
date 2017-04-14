// class github implements Serializable {
//   private String githubUrl
  
//   def setUrl(url) {
//     this.githubUrl = url
//   }
  
//   def createDeployment(target) {
//     echo "Deploying to ${target}"
//   }
// }

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

def sendPayload(url, payload) {
  writeFile file: 'payload.json', text: payload
  payload = null
  sh "curl -f -X POST -u ${githubUser}:${githubPassword} ${url} -d @payload.json \
              -H 'X-GitHub-Media-Type: application/vnd.github.ant-man-preview+json' > response.json"
  readJSON file: 'response.json'
}
  
def createDeployment(target, task = "deploy", description = "") {
  def rev = getGitCommit()
  echo "Deploying rev ${rev} to ${target}"
  
  def payload = """
    {
      "ref": "${rev}",
      "task": "${task}",
      "environment": "${target}",
      "description": "${description}"
    }
  """
  def response = sendPayload("${githubUrl}/deployments", payload)
  echo "response is ${response['url'].inspect()}"
  return response['url']
}

def createDeploymentStatus(url, state, target_url  = "", description = "", environment_url = "", auto_inactive = true){
  def rev = getGitCommit()
  echo "Updating status of ${url}"
  
  def payload = """
    {
      "state": "${state}",
      "log_url": "${target_url}",
      "description": "${description}",
      "environment_url": "${environment_url}",
      "auto_inactive": "${auto_inactive}"
    }
  """
  def response = sendPayload("${url}/statuses", payload)
  return response['url']
}