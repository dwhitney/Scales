package scales.plugin

import sbt._

import java.io.{File, FileOutputStream, InputStreamReader, FileInputStream}
import scala.io.Source

trait ScalesPlugin extends DefaultWebProject with WebXML{
	def scalesAppDir = path("src") / "main" / "scala" / "scales-app"
	def scalesResourcesDir = path("src") / "main" / "resources" / "scales-app"
	def webAppDir = path("src") / "main" / "webapp"
	def views = scalesAppDir / "views"
	def config = scalesAppDir / "conf"
	
	
	/**
	Initializes a Scales project directory structure
	**/
	def initAction = task{
		
		//check to see if it's already initialized
		var alreadyInitialized = false
		if(views.asFile.exists)	alreadyInitialized = true
		if(config.asFile.exists) alreadyInitialized = true
		if((config / "Settings.scala").asFile.exists) alreadyInitialized = true
		if(webAppDir.asFile.exists) alreadyInitialized = true
		if((webAppDir / "WEB-INF").asFile.exists) alreadyInitialized = true
		if((webAppDir / "WEB-INF" / "web.xml").asFile.exists) alreadyInitialized = true
				
		//if initialized send an error, else initialize!
		if(alreadyInitialized){
			Some("it looks like init was already called")
		}else{
			views.asFile.mkdirs
			config.asFile.mkdirs
			webAppDir.asFile.mkdirs
			
			//createWebXMLAction.run
			createSettings.run
			createIndexCode.run
			
			webAppDir.asFile.mkdirs
			(webAppDir / "WEB-INF").asFile.mkdirs
			FileUtilities.append((webAppDir / "WEB-INF" / "web.xml").asFile, webxml.toString, log)
			
			None
		}
	}
	
	/**
	creates the conf/Settings.scala file
	**/
	def createSettings = task {
		FileUtilities.append((config / "Settings.scala").asFile, Settings.settings, log)
		None
	}
	
	/**
	creates scales-app/pages/Index.scala
	**/
	def createIndexCode = task{
		FileUtilities.append((views / "Index.scala").asFile, IndexPage.page, log)
		None
	}
	
	/**
	Puts the web.xml from the WebXML trait in the webapp/WEB-INF directory
	**/
	def createWebXMLAction = task {
		webAppDir.asFile.mkdirs
		(webAppDir / "WEB-INF").asFile.mkdirs
		FileUtilities.append((webAppDir / "WEB-INF" / "web.xml").asFile, webxml.toString, log)
		None
	}
	
	/**
	Dispatches all of the scales commands
	**/
	def scalesAction = { 
		task{ args =>
			if(args.length < 1){
				task { Some("Usage: scales <action>") }
			}else{
				args(0) match {
					case "init" => initAction
					case _ => task { Some("args: " + args) }
				} 
			}
		}
	}
	
	lazy val scales = scalesAction
	
}