import sbt._

class SiteMeshProject(info: ProjectInfo) extends DefaultProject(info){
	
	val jasper = "org.apache.tomcat" % "jasper" % "6.0.20"
	val sitemesh = "opensymphony" % "sitemesh" % "2.4.2"
	
}