import sbt._

class SiteMeshProject(info: ProjectInfo) extends DefaultProject(info){
	
	val scalaTest = "org.scalatest" % "scalatest" % "0.9.5" % "test"
	val mockito = "org.mockito" % "mockito-all" % "1.8.0" % "test"
	val servlet = "javax.servlet" % "servlet-api" % "2.5" % "test,provided"
	val scalesCore = "scales" % "core" % "0.1" % "provided"
	val jasper = "org.apache.tomcat" % "jasper" % "6.0.20" % "provided"
	val sitemesh = "opensymphony" % "sitemesh" % "2.4.2" % "provided"
	
}