package scales

import scala.xml.NodeSeq
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

abstract class View(val request: HttpServletRequest, val response: HttpServletResponse){
	def mapping = request.getAttribute("mapping").asInstanceOf[Mapping[_]]
	def apply(): String
}