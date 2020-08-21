#!groovy

def workerNode = "devel10"

pipeline {
	agent {label workerNode}
	options {
		timestamps()
	}
	stages {
		stage("clear workspace") {
			steps {
				deleteDir()
				checkout scm
			}
		}
		stage("verify") {
			steps {
				withMaven(maven: 'Maven 3') {
					sh "mvn package"
				}
			}
		}
		stage("deploy") {
			when {
				branch "master"
			}
			steps {
				withMaven(maven: 'Maven 3') {
					sh "mvn jar:jar deploy:deploy"
				}
			}
		}
	}
}
