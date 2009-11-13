package scales.sitemesh

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scales.View
import scales.servlet.SettingsLoader

object SiteMesh{
	val SITE_MESH_DECORATOR = "__SITE_MESH_DECORATOR__"
}

trait SiteMesh extends SettingsLoader{ self: View =>
	def decorator: String = null
	def jsp: String

	def apply(): String = {
		if(decorator != null) request.setAttribute(SiteMesh.SITE_MESH_DECORATOR, this.decorator + ".jsp")
		val jspDir = callMethod[String]("jspDir").get
		val dispatcher = request.getRequestDispatcher(jspDir + jsp + ".jsp")
		request.setAttribute("model", this)
		dispatcher.forward(request, response)
		jsp
	}
}