package scales.plugin

import java.io.File

import org.scalatest._
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._

import sbt._

class ScalesPluginTests extends Spec with MustMatchers with BeforeAndAfter{
	
	val pluginRoot = "plugin"
		
	class TestProject(info: ProjectInfo) extends DefaultProject(info) with ScalesPlugin{
		scalesAppDir = path(pluginRoot) / "src" / "main" / "scala" / "scales-app"
		webAppDir = path(pluginRoot) / "src" / "main" / "webapp"
	}
	
	val project = new TestProject(ProjectInfo(new File("."), Nil, None)(new ConsoleLogger()))
	
	override def beforeEach(){
		FileUtilities.clean(project.scalesAppDir, new ConsoleLogger)
		FileUtilities.clean(project.webAppDir, new ConsoleLogger)
	}
	
	override def afterEach(){
		FileUtilities.clean(project.scalesAppDir, new ConsoleLogger)
		FileUtilities.clean(project.webAppDir, new ConsoleLogger)
	}
		
	describe("A ScalesPlugin"){
		
		it("must return a usage when called without any args"){
			val task = project.scales(Array[String]())
			task.run must equal(Some("Usage: scales <action>"))
		}	
		
		it("must init the pages, layouts, and components directory"){
			val task = project.scales(Array[String]("init"))
			task.run must equal(None)
			
			val pages = project.scalesAppDir / "pages"
			val layouts = project.scalesAppDir / "layouts"
			val components = project.scalesAppDir / "components"
			
			pages.asFile.exists must be(true)
			layouts.asFile.exists must be(true)
			components.asFile.exists must be(true)
			project.webAppDir.asFile.exists must be(true)
			
			(project.webAppDir / "WEB-INF" / "web.xml").asFile.exists must be(true)
			(project.webAppDir / "WEB-INF").asFile.exists must be(true)
		}
		
		it("must return an error if init is called but the project is already initialized"){
			//the first call initializes
			val task = project.scales(Array[String]("init"))
			task.run must equal(None)
			
			//now we'll call init a second time and should get an error
			project.scales(Array[String]("init")).run must equal(Some("it looks like init was already called"))
		}
	}
}