import sbt._

class ScalesProject(info: ProjectInfo) extends ParentProject(info)
{
	lazy val core = project("core")
	lazy val plugin = project("plugin", core)
	lazy val sensically = project("sensically")
	lazy val sitemesh = project("sitemesh", core)
	lazy val sitemeshPlugin = project("sitemesh.plugin")
	lazy val saysus = project("saysus", core, sitemesh)
	()
}