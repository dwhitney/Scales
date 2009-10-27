package scales.servlet

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import scala.xml._
import scales.conf.Config
import scales.conf.Config
import java.net.{URLClassLoader, URL}
import java.lang.reflect.Method
import scales.xml.AltParser


object ScalesServlet{
	val SCALES_CONFIG = "SCALES_CONFIG"
}

class ScalesServlet extends HttpServlet{
	
	private type URLMapping = (scala.util.matching.Regex, Class[P] forSome {type P <: Page})
	
	private val urlMappings: List[URLMapping] = getURLMappings
	private val pagePackage: String = "pages"
	
		
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
						e.printStackTrace
						None
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
				e.printStackTrace()
				null
			}
		}
	}
	
	override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
		if(urlMappings == null || pagePackage == null){
			println("the conf.Settings object was not found.  This should be in the scales-app/conf directory - maybe you changed the package name")
			request.setAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND, ScalesUrlMappingFilter.URL_NOT_FOUND)
		}else{
		
			val out = response.getWriter
			val incomingRequestURI = request.getAttribute(ScalesUrlMappingFilter.REQUEST_URI).toString //comes from the UrlMappingFilter
		
			//find a URL mapping and create the view - if the URL mapping isn't found, use the default which is /class/id.extension, 
			//if that isn't found, set a NOT_FOUND attribute in the request for further processing by the ScalesURLMappingFilter
			try{
				val (step1Passed: Boolean, page: Page) = urlMappings.find{tuple: URLMapping => tuple._1.pattern.matcher(incomingRequestURI).matches} match {
					case Some((regexp, clazz)) => {
						val page: Page = clazz.newInstance()
						//page.UrlVars = regexp
						(checkHttpStep(page, request, response), page)
					}
					case None => {
						val DefaultUrlVarsExtractor = """^/([^/]*?)/([^/]*?)(\..*)?$""".r
						try{
							//trying the default extractor - if it doesn't work, then we'll return a 404
							val DefaultUrlVarsExtractor(clazz, _, _) = request.getPathInfo()
							val page = Class.forName(pagePackage + "." + camelCaseClass(clazz)).newInstance().asInstanceOf[Page]
							(checkHttpStep(page, request, response), page)
					
						} catch {
							case e: scala.MatchError =>	{request.setAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND, ScalesUrlMappingFilter.URL_NOT_FOUND)}
						}
					}
				}
			
				if(step1Passed){
					page.action(request, response)
					val outputSeq = checkLayoutStep(page, request, response)
					outputSeq.foreach{ node: Node =>
						out.println(AltParser.toXML(node, false, false, false))
					}
				}else{
					response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
				}
			
			}catch{
				case me: scala.MatchError => {request.setAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND, ScalesUrlMappingFilter.URL_NOT_FOUND)}
			}
		}
		()
	}
	
	private def camelCaseClass(clazz: String): String = clazz.split("-").map(_.toLowerCase.capitalize).mkString

	//makes sure the Page is only displayed if the Methods specified in the class definition match the one requested.
	private def checkHttpStep(page: Page, request: HttpServletRequest, response: HttpServletResponse): Boolean = {
		page match {
			case p: GET if request.getMethod.toUpperCase == "GET" => true
			case p: PUT if request.getMethod.toUpperCase == "PUT" => true
			case p: POST if request.getMethod.toUpperCase == "GET" => true
			case p: DELETE if request.getMethod.toUpperCase == "DELETE" => true
			case p: HEAD if request.getMethod.toUpperCase == "HEAD" => true
			case p: OPTIONS if request.getMethod.toUpperCase == "OPTIONS" => true
			case p: CONNECT if request.getMethod.toUpperCase == "CONNECT" => true
			case p: TRACE if request.getMethod.toUpperCase == "TRACE" => true
			case p => p match {
				case h: HttpMethod => false
				case _ => true //this means no HttpMethod was specified, which means all methods are allowed
			}
		}		
	}
	
	private def checkLayoutStep(page: Page, request: HttpServletRequest, response: HttpServletResponse): NodeSeq = {
		page match {
			case p: UsesLayout[Layout] => {
				p.layout.newInstance.asInstanceOf[Layout].layoutPage(request, response, p)
			}
			case _ => page(request, response)
		}
	}
	
}