import sbt._

class ScalesCoreProject(info: ProjectInfo) extends DefaultProject(info){

	val scalaTest = "org.scalatest" % "scalatest" % "0.9.5" % "test"
	val mockito = "org.mockito" % "mockito-all" % "1.8.0" % "test"
	val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"

	val repo1 = "maven-repo" at "http://repo2.maven.org/maven2/"

}