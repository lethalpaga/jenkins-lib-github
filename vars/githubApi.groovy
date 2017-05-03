import groovy.json.JsonOutput

// Sends an HTTP query to github
def sendPayload(url, payload, method = "POST") {
  writeFile file: 'payload.json', text: groovy.json.JsonOutput.toJson(payload)
  sh """
  set +x
  curl -f -X ${method} -u '${githubUser}:${githubPassword}' '${url}' -d @payload.json \
              -H 'X-GitHub-Media-Type: application/vnd.github.ant-man-preview+json' > response.json
  set -x
  """
  readJSON file: 'response.json'
}

// Get the HEAD git commit
// See also getPullRequestHead to reliably get the last commit of a PR
def getGitCommit() {
  sh(returnStdout:true, script: "git rev-parse HEAD").trim()
}

// Returns the latest SHA of a PR
// This can be useful when building merge PRs
// as the HEAD commit won't exist outside of jenkins
def getPullRequestHead(pr_number) {
  def pr = sendPayload("${githubApiRepoUrl()}/pulls/${pr_number}", "", "GET")
  pr["head"]["sha"]
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

// Returns the Repos base endpoint
private githubApiRepoUrl() {
  "${githubUrl}/repos/${githubOrg}/${githubRepo}"
}

// Create a new deployement
// @params Hash containing the parameter as per https://developer.github.com/v3/repos/deployments/#create-a-deployment
def createDeployment(Map payload) {
  def response = sendPayload("${githubApiRepoUrl()}/deployments", payload)
  return response['url']
}

// Send a new deployement status
// @param url Base url to the deployment as returned by createDeployment)
// @param payload Status parameters map as per https://developer.github.com/v3/repos/deployments/#create-a-deployment-status     
def createDeploymentStatus(url, Map payload){
  echo "Updating deployment status of ${url} with ${payload}"

  def response = sendPayload("${url}/statuses", payload)
  return response['url']
}

// Creates a new Status
// @param payload Status payload as per https://developer.github.com/v3/repos/statuses/#create-a-status
def createStatus(String sha, Map payload) {
  def url = "${githubApiRepoUrl()}/statuses/${sha}"
  echo "Updating status of ${url} with ${payload}"

  def response = sendPayload(url, payload)
  return response['url']
}

// Create a new issue comment. This also work for global comments on PR
// @param issueNumber ID of the issue or PR
// @param payload Payload as per https://developer.github.com/v3/pulls/comments/#create-a-comment
def createIssueComment(issueNumber, Map payload) {
  def url = "${githubApiRepoUrl()}/issues/${issueNumber}/comments"
  echo "Creating comment on ${url} with ${payload}"

  def response = sendPayload(url, payload)
  return response['url']
}