package scales

import scala.util.matching.Regex
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
This is a standard mapping of a regex to a class extending View.
**/
case class Mapping[V <: View](val mapping: String, val view: Class[V]){
	//this is here to prevent having to generate the Regex every time the r method is called
	private lazy val reg = mapping.r
	
	/**
	returns the Regex generated from the mapping string passed in the constructor
	**/
	def r: Regex = reg

}


/**
This is a mapping that mirrors the @RequestMapping in Spring 3.0, so you use it like
/hotels/{hotelId} or /hotels/{hotel}/bookings/{booking}

In the first example a URL like /hotels/5 would map "5" to the hotelId variable

**/
case class TemplateMapping[V <: View](override val mapping: String, override val view: Class[V]) extends Mapping(mapping, view){
	
	//this lazy val is here to prevent generating the Regex every time r is called
	private lazy val reg = buildR
	override def r = reg
	
	/**
	returns a Regex usable for extracting var names from the URL
	**/
	private def buildR: Regex = {
		val url = mapping
		
		//replaces {varName} with ([^/]*?) so we can capture the variables with a regular expression
		//as an example /hotels/{hotelId} becomes /hotel/([^/]*?)
		val regex = """\{(.*?)\}""".r.replaceAllIn(url, "([^/]*?)")
		
		//check to see if the url ends in a variable name like /foo/{bar}
		//if it does, then we don't want to return any extension like .html, so we modify the regex to leave that out
		if(url(url.length - 1) == '}') (regex.substring(0, regex.length - 8) + "([^.]*).*").r 
		else regex.r
	}
	
	//this lazy val is here to prevent having to generate the variable names every time the variableNames method is called
	private lazy val varNames = buildVarNames
	def variableNames = varNames
	
	/**
	returns a Seq[String] of all the variable names declared in the url constructor arg, for example:
	/hotels/{hotelId}/booking/{bookingId} will result in: Seq("hotelId", "bookingId")
	**/
	private def buildVarNames: Seq[String] = {
		"""\{(.*?)\}""".r.findAllIn(mapping).collect.map(_.toString.replaceAll("[{}]", "")) 
	}
}

/**
A trait that allows one to define an action that occurs before the view is created/rendered
**/
trait MappingFilter{ self: Mapping[_] =>
	/**
	The action that occurs before the view is created/rendered.  If you return true then the view will be created/rendered
	If false is returned, the view will not be created/rendered
	**/
	def beforeView(request: HttpServletRequest, response: HttpServletResponse): Boolean

}