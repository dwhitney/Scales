package scales.servlet

import javax.servlet.{Filter, FilterConfig, FilterChain, ServletRequest, ServletResponse}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import java.net.{URL,URLClassLoader}
import java.io.File

import scales.conf.Config

import scala.xml.NodeSeq

/**
This class filters URLs based upon the mappings defined in scales-app/conf/Settings.scala.
If a matching URL mapping is found, it's processed by the Scales framework.  If not, it's 
passed along the filter chain for further processing by the servlet container, and/or other
filters.
**/
class ScalesFilter extends Filter with SettingsLoader with ViewBuilder{
	
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
		val mapping = urlMappings.find{mapping: Mapping[_ <: View] => mapping.r.pattern.matcher(requestURI).matches}
		
		//see if we can build a page - if so write it to the response
		mapping match {
			case Some(m: Mapping[_]) => 
				buildView(request, response, m) match {
					case Some(page: String) => response.getWriter.print(page)
					case None => filterChain.doFilter(request, response)
 				}
			case None => filterChain.doFilter(request, response)
		}
		
		()
	}
	
	
	/**
	loads all of the pages that have mappings from the mappings.properties file
	**/
	def init(filterConfig: FilterConfig): Unit = {
		loadCompiledFilesIntoClassLoader
		this.filterConfig = filterConfig
	}
	
	//loads the compiled classes into the class loader - I believe this is neccesary because this filter comes from a jar file
	//and the compiled classes come from the user's project
	private def loadCompiledFilesIntoClassLoader(){
		val sysloader = this.getClass.getClassLoader().asInstanceOf[URLClassLoader];
		val sysclass = classOf[URLClassLoader]
		val parameters = scala.Array[Class[URL]](classOf[URL])
		val method = sysclass.getDeclaredMethod("addURL", classOf[URL]);
		method.setAccessible(true);
		method.invoke(sysloader, new File("sensically/target/classes").toURL)
	}
	
	/**
	required by the Filter trait - for now it does nothing
	**/
	def destroy(): Unit = {}
	
}

