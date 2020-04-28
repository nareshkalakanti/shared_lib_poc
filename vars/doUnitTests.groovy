def call(args = " ") {
	echo 'Running unit tests...'
	sh "${MAVEN} test -Dmaven.static.skip=true ${args}"
}