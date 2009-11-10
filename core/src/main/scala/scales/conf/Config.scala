package scales.conf

import scala.util.matching.Regex
import java.util.Properties
import java.io.IOException

abstract class Environment{}
case object Development extends Environment{}
case object Test extends Environment{}
case object Production extends Environment{}

trait Config{
	
	/**
	Returns a List that contains tuples of Regexs and Views associated with them
	**/
	def urlMappings: List[_ <: Mapping[_ <: View]]
	// = ("""^/([^/]*?)/([^/]*?)(\..*)?$""".r, classOf[View]) :: Nil
	
	/**
	The packages that the views are in
	**/
	def viewPackages: List[String] = "views" :: Nil
	
	
	/**
	Returns the environment 
	**/
	def environment: Environment = {
		
		System.getProperty("environment", "development") match {
			case "test" => Test
			case "production" => Production
			case _ => Development
		}
		
	}
}