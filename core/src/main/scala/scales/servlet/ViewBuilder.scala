package scales.servlet

import scala.xml.NodeSeq
import scales._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
trait that builds pages.  If the Page is contained by the framework, Some(NodeSeq) of that page will be returned,
otherwise None is returned.  The NodeSeq is the rendered product of the page 
**/
trait ViewBuilder{
	
	/**
	performs the logic of building a Page, including processing the layout.
	If the page cannot be built, None is returned, otherwise Some(page.apply()) is returned
	**/
	def buildView(request: HttpServletRequest, response: HttpServletResponse, mapping: Mapping[_ <: View]): Option[String] = {
		//build the given view with reflection
		try{
			val constructor = mapping.view.getConstructor(classOf[HttpServletRequest], classOf[HttpServletResponse])
			val view = constructor.newInstance(request, response)
			restFilter(request, view) match {
				case Some(view: View) => Some(view())
				case None =>
					response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
					None
			}
		} catch {
			case e: Exception => 
				println("Exception caught when creating view"); 
				e.printStackTrace
				None
		}
	}
	
	//figures out which, if any, HTTP methods are allowed on the given view
	private def restFilter(request: HttpServletRequest, view: View): Option[View] = {
		val method = REST.getMethod(request)
		view match {
			case rest: REST => rest.methods match {
				case Nil => Some(view)
				case _ if(rest.methods.contains(method)) => Some(view)
				case _ => None
				
			}
			case _ => Some(view)
		}
	}
}