package scales

import scala.xml.NodeSeq
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

trait View{
	
	def apply(request: HttpServletRequest, response: HttpServletResponse): NodeSeq = {
		val name = this.getClass.getName
		val v = Class.forName("generated." + name).newInstance.asInstanceOf[View]
		v(request, response)
	}
	
	def action(request: HttpServletRequest, response: HttpServletResponse): Unit = {}
}