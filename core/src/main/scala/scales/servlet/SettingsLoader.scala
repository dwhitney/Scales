package scales.servlet

import scales.conf.Config
import ScalesFilter._
import java.lang.reflect._

/**
Loads settings from scales-app/conf/Settings.scala and returns a Config object from settings
**/
trait SettingsLoader{
	
	def urlMappings: List[URLMapping] = getURLMappings
	def pagePackage: String = getPagePackage
	
		
	//looks for a class called conf.Settings - this would come from a scales project
	//if it's found, the URL Mappings are pulled from it, and returned
	private def getURLMappings: List[URLMapping] = {
		callMethod[List[URLMapping]]("urlMappings") match {
			case Some(l: List[URLMapping]) => l
			case None => null
		}
	}
	
	//Loads the pagePackage from conf.Settings - same process as described above getURLMappings
	private def getPagePackage: String = {
		callMethod[String]("pagePackage") match {
			case Some(s: String) => s
			case None => null
		}
	}
	
	//calls a static method on the conf.Settings object and returns the result
	private def callMethod[T](methodName: String): Option[T] = {
		loadSettings match {
			case null => None
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