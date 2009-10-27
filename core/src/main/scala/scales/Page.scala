package scales

import scala.util.matching.Regex
import javax.servlet.http.HttpServletResponse

trait Page extends View{
	def redirect(response: HttpServletResponse, url: String): Unit = {
		response.sendRedirect(url)
	}
}
