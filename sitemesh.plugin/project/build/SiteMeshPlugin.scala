import sbt._

import scala.xml._
import scala.xml.transform._
import java.io.File

class SiteMeshPlugin(info: ProjectInfo) extends PluginProject(info){
	
	val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.19" % "test"
	val scalaTest = "org.scalatest" % "scalatest" % "0.9.5" % "test"
	val mockito = "org.mockito" % "mockito-all" % "1.8.0" % "test"
	val core = "scales" % "core" % "0.1" 
	val scalesSitemesh = "scales" % "sitemesh" % "0.1"
	
}