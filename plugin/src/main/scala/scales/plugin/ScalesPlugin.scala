package scales.plugin

import sbt._

trait ScalesPlugin extends BasicScalaProject with WebXML{
	var scalesAppDir = path("src") / "main" / "scala" / "scales-app"
	var webAppDir = path("src") / "main" / "webapp"
	
	/**
	Initializes a Scales project directory structure
	**/
	def initAction = task{
		val pages = scalesAppDir / "pages"
		val layouts = scalesAppDir / "layouts"
		val components = scalesAppDir / "components"
		
		//check to see if it's already initialized
		var alreadyInitialized = false
		if(pages.asFile.exists)	alreadyInitialized = true
		if(layouts.asFile.exists) alreadyInitialized = true
		if(components.asFile.exists) alreadyInitialized = true
		if(webAppDir.asFile.exists) alreadyInitialized = true
		if((webAppDir / "WEB-INF").asFile.exists) alreadyInitialized = true
		if((webAppDir / "WEB-INF" / "web.xml").asFile.exists) alreadyInitialized = true
				
		//if initialized send an error, else initialize!
		if(alreadyInitialized){
			Some("it looks like init was already called")
		}else{
			pages.asFile.mkdirs
			layouts.asFile.mkdirs
			components.asFile.mkdirs
			createWebXMLAction.run
			None
		}
	}
	
	/**
	Puts the web.xml from the WebXML trait in the webapp/WEB-INF directory
	**/
	def createWebXMLAction = task {
		FileUtilities.clean((webAppDir / "WEB-INF" / "web.xml"), log);
		webAppDir.asFile.mkdirs
		(webAppDir / "WEB-INF").asFile.mkdirs
		FileUtilities.append((webAppDir / "WEB-INF" / "web.xml").asFile, webxml.toString, log)
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
	
	override def compileAction = super.compileAction dependsOn(createWebXMLAction)

}