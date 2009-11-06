package scales.plugin

/**
Assets for the Index and Ajax pages created when <i>scales init</i> is called
**/
object IndexPage{
	
	/**
	The code for Index.scala - contains two clases, Index and Ajax
	**/
	def page: String = {
		"""
package views

import _root_.scales.View
import _root_.javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import _root_.scala.xml.NodeSeq

class Index(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response){
	override def apply() = {
		"<h1>Hello, World!</h1>"
	}
}
		
"""
	}
}