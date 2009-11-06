package scales

import scala.xml.NodeSeq
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

abstract class View(request: HttpServletRequest, response: HttpServletResponse){
	def apply(): String
}