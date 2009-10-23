package scales.servlet

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import scala.xml._
import scales.conf.Config

class ScalesServlet extends HttpServlet{
	
	override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
		val out = response.getWriter
		val incomingRequestURI = request.getAttribute(ScalesUrlMappingFilter.REQUEST_URI).toString //comes from the UrlMappingFilter
		
		//find a URL mapping and create the view - if the URL mapping isn't found, use the default which is /class/id.extension, 
		//if that isn't found, set a NOT_FOUND attribute in the request for further processing by the ScalesURLMappingFilter
		try{
			val (step1Passed: Boolean, page: Page) = Config.mappings.find(tuple => tuple._1.pattern.matcher(incomingRequestURI).matches) match {
				case Some((regexp, clazz)) => {
					val page: Page = clazz.newInstance
					page.UrlVars = regexp
					(checkHttpStep(page, request, response), page)
				}
				case None => {
					val DefaultUrlVarsExtractor = """^/([^/]*?)/([^/]*?)(\..*)?$""".r
					try{
						//trying the default extractor - if it doesn't work, then we'll return a 404
						val DefaultUrlVarsExtractor(clazz, _, _) = request.getPathInfo()
						val page = Class.forName(Config.pagePackage + "." + camelCaseClass(clazz)).newInstance().asInstanceOf[Page]
						(checkHttpStep(page, request, response), page)
					
					} catch {
						case e: scala.MatchError =>	{request.setAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND, ScalesUrlMappingFilter.URL_NOT_FOUND)}
					}
				}
			}
			
			if(step1Passed){
				val outputSeq = checkLayoutStep(page, request, response)
				out.println(outputSeq)
			}else{
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
			}
			
		}catch{
			case me: scala.MatchError => {request.setAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND, ScalesUrlMappingFilter.URL_NOT_FOUND)}
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
				val layout = p.layout.newInstance().asInstanceOf[Layout]
				layout.layoutPage(request, response, p)
			}
			case _ => page(request, response)
		}
	}
	
}