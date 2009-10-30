package scales.servlet

import javax.servlet.{Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import scales.conf.Config
import scales.Page

import scala.xml.NodeSeq

/**
Defines some constants
**/
object ScalesFilter{
	/**
	convenience type, so I don't have to type that out a lot
	**/
	type URLMapping = (scala.util.matching.Regex, Class[P] forSome {type P <: Page})
}

/**
This class filters URLs based upon the mappings defined in scales-app/conf/Settings.scala.
If a matching URL mapping is found, it's processed by the Scales framework.  If not, it's 
passed along the filter chain for further processing by the servlet container, and/or other
filters.
**/
class ScalesFilter extends Filter with SettingsLoader with PageBuilder{
	
	//the filterConfig - set by the init method
	private var filterConfig: FilterConfig = null
	
	/**
	required by the Filter trait, this method is called by the servlet container and performs all of the logic
	for figuring out if a given request should be handled by the Scales framework
	**/
	def doFilter(req: ServletRequest, resp: ServletResponse, filterChain: FilterChain): Unit = {
		//cast request and response to their HTTP variants
		val request = req.asInstanceOf[HttpServletRequest]
		val response = resp.asInstanceOf[HttpServletResponse]

		//get requestURI, strip out the contextPath, and find a mapping for it
		val requestURI = request.getRequestURI.replaceFirst("^" + request.getContextPath, "")
		val mapping = urlMappings.find{tuple: ScalesFilter.URLMapping => tuple._1.pattern.matcher(requestURI).matches}
		
		//see if we can build a page - if so write it to the response
		mapping match {
			case Some(m: ScalesFilter.URLMapping) => 
				buildPage(request, response, m) match {
					case Some(page: NodeSeq) => 
						response.getWriter.print(page)
					case None => filterChain.doFilter(request, response)
 				}
			case None => filterChain.doFilter(request, response)
		}
		
		()
	}
	
	
	/**
	required by the Filter trait - for now it only sets the filter config
	**/
	def init(filterConfig: FilterConfig): Unit = this.filterConfig = filterConfig
	
	/**
	required by the Filter trait - for now it does nothing
	**/
	def destroy(): Unit = {}
	
}

