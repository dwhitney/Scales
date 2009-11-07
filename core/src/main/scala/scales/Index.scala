package scales

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class Index(request: HttpServletRequest, response: HttpServletResponse)	extends View(request, response) with Mapping{
		override def mapping = "/hotels/{hotel}/bookings/{booking}"
		override def apply = "Index!"
		
		class Foo(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with Mapping{
				override def mapping = "/hotels/{hotel}/bookings/{booking}"
				override def apply = "Index!"
		}
}

class Bar(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with TemplateMapping{
		override def mapping = "/hotels/{hotel}/bookings/{booking}"
		override def apply = "Index!"
}