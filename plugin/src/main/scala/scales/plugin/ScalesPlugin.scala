package scales.plugin

import sbt._

import java.io.{File, FileOutputStream, InputStreamReader, FileInputStream}
import scala.io.Source
import scala.actors.Futures._

import scales.xml.ScalesHandler

trait ScalesPlugin extends DefaultWebProject with WebXML{
	def scalesAppDir = path("src") / "main" / "scala" / "scales-app"
	def scalesResourcesDir = path("src") / "main" / "resources" / "scales-app"
	def webAppDir = path("src") / "main" / "webapp"
	def pages = scalesAppDir / "pages"
	def layouts = scalesAppDir / "layouts"
	def components = scalesAppDir / "components"
	def config = scalesAppDir / "conf"
	
	//managed sources - where the generated code goes
	def managedSources = path("src_managed") / "main"
	def managedScalaPath = managedSources / "scala"
	def managedGeneratedScalaCode = managedScalaPath / "generated"
	def managedResourcesPath = managedSources / "resources"
	
	override def mainSourceRoots = super.mainSourceRoots +++ managedScalaPath +++ managedGeneratedScalaCode
	override def mainResources = super.mainResources +++ descendents(managedResourcesPath ##, "*")
	override def compileAction = super.compileAction dependsOn(createWebXMLAction) dependsOn(scalesGenerateViews)
	override def cleanAction = super.compileAction dependsOn(scalesCleanGeneratedViews)
	
	
	/**
	Initializes a Scales project directory structure
	**/
	def initAction = task{
		
		val resourcesPages = scalesResourcesDir / "pages"
		val resourcesLayouts = scalesResourcesDir / "layouts"
		val resourcesComponents = scalesResourcesDir / "components"
		
		//check to see if it's already initialized
		var alreadyInitialized = false
		if(pages.asFile.exists)	alreadyInitialized = true
		if(layouts.asFile.exists) alreadyInitialized = true
		if(components.asFile.exists) alreadyInitialized = true
		if(config.asFile.exists) alreadyInitialized = true
		if((config / "Settings.scala").asFile.exists) alreadyInitialized = true
		if(resourcesPages.asFile.exists) alreadyInitialized = true
		if(resourcesLayouts.asFile.exists) alreadyInitialized = true
		if(resourcesComponents.asFile.exists) alreadyInitialized = true
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
			config.asFile.mkdirs
			
			resourcesPages.asFile.mkdirs
			resourcesLayouts.asFile.mkdirs
			resourcesComponents.asFile.mkdirs
						
			createWebXMLAction.run
			createSettings.run
			None
		}
	}
	
	/**
	creates the conf/Settings.scala file
	**/
	def createSettings = task {
		val settings = """
package conf

import scales.Page
import scales.conf.Config
import pages.Index
import scala.util.matching.Regex

object Settings extends Config{
	/*
	This type is a tuple with the first element being a regular expression extractor 
	that matches a URL and the second element being any class that extends the Page trait.
	An instance of the class will be created and invoked when the URL of a given request  
	matches the regular expression
	*/
	type URLMapping = (Regex, Class[P] forSome {type P <: Page})

	/*
	examples of URL schemes:
	listed above: """ + "\"\"\"" + """(/""|/index.html)""" + "\"\"\"" + """.r -- will match / or /index.html
	"/index.html".r -- will only match /index.html
	""" + "\"\"\"" + """(/""|/index(\.?.{0,4}))""" + "\"\"\"" + """.r -- will match / or /index.html or /index or /index.htm (or any 4 letter extension)
	""" + "\"\"\"" + """^/([^/]*?)/([^/]*?)(\..*)?$""" + "\"\"\"" + """.r  -- will match /class/id.html or /class/id.html
	*/

	def urlMappings: List[URLMapping] = (""" + "\"\"\"" + """(/""|/index.html)""" + "\"\"\"" + """.r, classOf[Index]) :: Nil

}		
"""
		FileUtilities.append((config / "Settings.scala").asFile, settings, log)
		None
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
	
	lazy val scalesCleanGeneratedViews = task {
		FileUtilities.clean(managedGeneratedScalaCode, log)
	}
	
	lazy val scalesGenerateViews = task { 
			
		val generatedPages = managedGeneratedScalaCode / "pages"
		val generatedLayouts = managedGeneratedScalaCode / "layouts"
		val generatedComponents = managedGeneratedScalaCode / "components"
		
		val pages = scalesResourcesDir / "pages"
		val layouts = scalesResourcesDir / "layouts"
		val components = scalesResourcesDir / "components"
		
		FileUtilities.createDirectory(generatedPages, log)
		FileUtilities.createDirectory(generatedLayouts, log)
		FileUtilities.createDirectory(generatedComponents, log)
		
		val fp = future{ buildFiles(pages.asFile, generatedPages, "pages") }
		val lp = future{ buildFiles(layouts.asFile, generatedLayouts, "layouts") }
		val cp = future{ buildFiles(components.asFile, generatedComponents, "components") }
		
		fp()
		lp()
		cp()
		
		None 
	} dependsOn (scalesCleanGeneratedViews)
		
	def buildFiles(file: File, generatedPath: Path, packageName: String){
		if(file.isDirectory){
			val someFutures = file.list().map{ name =>
				future{ makeFile(new File(file.getPath() + "/" + name), new File(generatedPath.asFile.getPath + "/" + name.replaceFirst("\\..*$", ".scala")), packageName) }
			}
			someFutures.foreach{ _() }
		}
	}
	
	private def makeFile(inFile: File, outFile: File, packageName: String){
		log.info("Creating " + outFile.getPath)  
		val a = inFile.getPath.split("/") //temp array to extract className
		val className = a(a.size - 1).replaceFirst("\\..*", "")
				
		val head = """package generated.""" + packageName + """

import scala.xml.NodeSeq
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

//DO NOT MODIFY - THIS FILE IS AUTOGENERATED AND ANY CHANGES WILL BE LOST
object """ + className + """{
	def apply(request: HttpServletRequest, response: HttpServletResponse): NodeSeq = {
"""
		val body = """
	}
}

class """ + className + """ extends _root_.""" + packageName + """.""" + className + """{
	override def apply(request: HttpServletRequest, response: HttpServletResponse): NodeSeq = {
		""" + className + """(request, response)"""

		val foot = "\n\t}\n}"
		FileUtilities.append(outFile, head, log)
		FileUtilities.append(outFile, ScalesHandler.parse(inFile), log)
		FileUtilities.append(outFile, body, log)
		FileUtilities.append(outFile, foot, log)
	}
	
}