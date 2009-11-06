package scales

import scala.util.matching.Regex

trait Mapping{ self: View =>
	def mapping: String
	def r: Regex = mapping.r
}


/**
This is a mapping that mirrors the @RequestMapping in Spring 3.0, so you use it like
/hotels/{hotelId} or /hotels/{hotel}/bookings/{booking}

In the first example a URL like /hotels/5 would map "5" to the hotelId variable

**/
trait TemplateMapping extends Mapping{ self: View =>
	
	/**
	returns a Regex usable for extracting var names from the URL
	**/
	override def r: Regex = {
		val url = mapping
		
		//replaces {varName} with ([^/]*?) so we can capture the variables with a regular expression
		//as an example /hotels/{hotelId} becomes /hotel/([^/]*?)
		val regex = """\{(.*?)\}""".r.replaceAllIn(url, "([^/]*?)")
		
		//check to see if the url ends in a variable name like /foo/{bar}
		//if it does, then we don't want to return any extension like .html, so we modify the regex to leave that out
		if(url(url.length - 1) == '}') (regex.substring(0, regex.length - 8) + "([^.]*).*").r 
		else regex.r
	}
	
	/**
	returns a Seq[String] of all the variable names declared in the url constructor arg, for example:
	/hotels/{hotelId}/booking/{bookingId} will result in: Seq("hotelId", "bookingId")
	**/
	def variableNames: Seq[String] = {
		"""\{(.*?)\}""".r.findAllIn(mapping).collect.map(_.toString.replaceAll("[{}]", "")) 
	}
}

/**
A trait that allows one to define an action that occurs before the view is created/rendered
**/
trait MappingFilter{ self: Mapping =>
	/**
	The action that occurs before the view is created/rendered.  If you return true then the view will be created/rendered
	If false is returned, the view will not be created/rendered
	**/
	def beforeView: Boolean

}