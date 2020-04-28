def call(){
	git url: jobProperties.gitRepoHttpsUrl, branch: jobProperties.gitRepoBranchName, credentialsId: 'STASH_CREDENTIAL' // TODO - constant

	// pipeline job has started successfully

	jobStatus.startedSuccessfully = true

	// merge if pull request (and throw an exception if merge fails) - TODO
}