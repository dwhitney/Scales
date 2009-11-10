package scales

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class Index(request: HttpServletRequest, response: HttpServletResponse)	extends View(request, response){
		override def apply = "Index!"
}