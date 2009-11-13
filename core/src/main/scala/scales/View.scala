package scales

import scala.xml.NodeSeq
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

abstract class View(val request: HttpServletRequest, val response: HttpServletResponse){
	def apply(): String
}