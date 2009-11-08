import sbt._

class ScalesProject(info: ProjectInfo) extends ParentProject(info)
{
	lazy val core = project("core")
	lazy val plugin = project("plugin", core)
	lazy val sensically = project("sensically")
	()
}