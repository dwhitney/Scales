import sbt._

class ScalesPluginProject(info: ProjectInfo) extends PluginProject(info){
	
	val scalaTest = "org.scalatest" % "scalatest" % "0.9.5" % "test"
	val mockito = "org.mockito" % "mockito-all" % "1.8.0" % "test"
	val jline = "jline" % "jline" % "0.9.94" % "test"
}