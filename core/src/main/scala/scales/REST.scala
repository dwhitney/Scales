package scales

import javax.servlet.http.HttpServletRequest

/**
Trait used for building RESTful services and actions of that kind
**/
trait REST{
	/**
	Accepted HTTP Methods. 
	The default is Nil, which means all methods are accepted
	**/
	def methods: List[Method] = Nil
}

/**
Singleton object with some convenience methods for RESTful Services
**/
object REST{
	
	/**
	Takes an HttpServletRequest and returns a case object representing the HTTP method requested
	**/
	def getMethod(request: HttpServletRequest): Method = {
		request.getMethod.toUpperCase match {
			case "GET" => GET
			case "POST" => POST
			case "PUT" => PUT
			case "DELETE" => DELETE
			case "HEAD" => HEAD
			case "OPTIONS" => OPTIONS
			case "CONNECT" => CONNECT
			case "TRACE" => TRACE
		}
	}
}

/**
Abstract class representing an HTTP Method - likely to only be extended by case classes/object
**/
abstract class Method{}

/**
case object representing an HTTP GET method
**/
case object GET extends Method{}

/**
case object representing an HTTP POST method
**/
case object POST extends Method{}

/**
case object representing an HTTP PUT method
**/
case object PUT extends Method{}

/**
case object representing an HTTP DELETE method
**/
case object DELETE extends Method{}

/**
case object representing an HTTP HEAD method
**/
case object HEAD extends Method{}

/**
case object representing an HTTP OPTIONS method
**/
case object OPTIONS extends Method{}

/**
case object representing an HTTP CONNECT method
**/
case object CONNECT extends Method{}

/**
case object representing an HTTP TRACE method
**/
case object TRACE extends Method{}