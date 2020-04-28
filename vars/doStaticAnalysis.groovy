def call(args = " ") {
	echo 'Performing static analysis...'
	sh "${MAVEN} clean compile ${args}"

	script {
        	generateReportOnFailure = true
	}
}