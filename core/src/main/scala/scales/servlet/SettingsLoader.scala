package scales.servlet

import scales.conf.Config
import java.lang.reflect._

/**
Loads some settings from scales-app/conf/Settings.scala
**/
trait SettingsLoader{
		
	/**
	returns the URL Mappings found in the conf.Settings object supplied by the user
	the mappings are used to decide if the filter should build any pages.
	**/
	def urlMappings: List[Mapping[_ <: View]] = mappingsVal
	private lazy val mappingsVal = getURLMappings
	
			
	/**
	calls a static method on the conf.Settings object and returns the result
	**/
 	def callMethod[T](methodName: String): Option[T] = {
		loadSettings match {
			case c: Class[Any] => {
				try{
					c.getMethods.find{ _.getName == methodName} match {
						case Some(method: Method) => Some(method.invoke(null).asInstanceOf[T])
						case None => None
					}
				} catch {
					case e: Exception => {
						println("conf.Settings was not found.  This should be in the scales-app/conf directory - maybe you changed the package name")
						throw e
					}
				}
			}
		}
	}
	
	//looks for a class called conf.Settings - this would come from a scales project
	//if it's found, the URL Mappings are pulled from it, and returned
	private def getURLMappings: List[Mapping[_ <: View]] = {
		callMethod[List[Mapping[_ <: View]]]("urlMappings") match {
			case Some(l: List[Mapping[_]]) => l
			case None => null
		}
	}
	
	
	//loads conf.Settings, which should be in the scales-app/conf directory in the app using scales - it does not exist
	//in the scales libraries, but rather it's configured by the client of the application
	private def loadSettings: Class[T] forSome { type T <: Any} = {
		try{
			this.getClass.getClassLoader.loadClass("conf.Settings") //this should extend the scales.conf.Config trait
		}catch{
			case e: java.lang.ClassNotFoundException => {
				println("conf.Settings was not found.  This should be in the scales-app/conf directory - maybe you changed the package name")
				throw e
			}
		}
	}
	
}