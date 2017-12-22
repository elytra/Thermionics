node {
	checkout scm
	sh './gradlew setupCiWorkspace clean build --refresh-dependencies'
	archive 'build/libs/*jar'
}
