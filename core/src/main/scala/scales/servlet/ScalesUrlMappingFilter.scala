package scales.servlet

import javax.servlet.{Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

object ScalesUrlMappingFilter{
	val CALLED = "ScalesUrlFilterCalledOnce"
	val URL_NOT_FOUND = "ScalesURLNotFound"
	val REQUEST_URI = "SCALES_REQUEST_URI"
}

/**
This class checks to see if the servlet can process the request.  If it can, then great. If it can't then the filterChain
proceeds looking for whatever else can process the request. 
**/
class ScalesUrlMappingFilter extends Filter{
	
	private var filterConfig: FilterConfig = null
	
	/**
	Filters the request by looking for Mapped URLs.  If no URL is mapped, the request is passed along the filter chains
	**/
	def doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain): Unit = {
		//this filter should only be called once per request
		if(request.getAttribute(ScalesUrlMappingFilter.CALLED) == ScalesUrlMappingFilter.CALLED){
			if(filterChain != null){
				filterChain.doFilter(request, response)
			}
		}else{
			request.setAttribute(ScalesUrlMappingFilter.CALLED, ScalesUrlMappingFilter.CALLED)
			val requestURI = request.asInstanceOf[HttpServletRequest]
			.getRequestURI
			.replaceFirst("^" + request.asInstanceOf[HttpServletRequest]
			.getContextPath, "")
			request.asInstanceOf[HttpServletRequest].setAttribute(ScalesUrlMappingFilter.REQUEST_URI, requestURI) //used by the servlet to parse the URL
			
			filterConfig
				.getServletContext()
				.getRequestDispatcher("/scales.dispatch")
				.include(request, response) //send request to servlet to see if it can be processed
			
			//if the servlet was unable to service this request it sets a NOT_FOUND attribute
			if(request.getAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND) == ScalesUrlMappingFilter.URL_NOT_FOUND){
				if(filterChain != null){
					filterChain.doFilter(request, response)
				}
			}
		}
	}
	
	def init(filterConfig: FilterConfig): Unit = {
		this.filterConfig = filterConfig		
	}
	def destroy(): Unit = {}
	
}