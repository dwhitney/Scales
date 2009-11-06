package scales

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}


class MappingTests extends Spec with MustMatchers{

	//setup some testing stubs
	class TestView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response){
		def apply() = "Test!"
	}
	val mockRequest = mock(classOf[HttpServletRequest])
	val mockResponse = mock(classOf[HttpServletResponse])

	describe("A Mapping"){
			
		it("must return a regex and we'll check to see if it works"){
			
			val view = new TestView(mockRequest, mockResponse) with Mapping{
				def mapping = """^/([^/]*?)/([^/]*?)(\..*)?$"""
			}
			val view.r(one, two, three) = "/one/two.three"
			one must equal("one")
			two must equal("two")
			three must equal(".three")
		}		
	}
	
	describe("A TemplateMapping"){
		
		it("must turn a template like /hotels/{hotelId} into a usable regex"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/hotels/{hotelId}"
			}
			val view.r(hotelId) = "/hotels/5"
			hotelId must equal("5")
			()
		}
		
		it("must turn a template like /hotels/{hotelId}/booking/{bookingId}/foo into a usable regex"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/hotels/{hotelId}/booking/{bookingId}/foo"
			}
			val view.r(hotelId, bookingId) = "/hotels/5/booking/10/foo"
			hotelId must equal("5")
			bookingId must equal("10")
		}
		
		it("must turn a template like /hotels/{hotelId}/booking/{bookingId} into a usable regex"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/hotels/{hotelId}/booking/{bookingId}"
			}
			val view.r(hotelId, bookingId) = "/hotels/5/booking/10"
			hotelId must equal("5")
			bookingId must equal("10")
		}
		
		it("must turn a template into vars even if the url contains an extension.  That extension should not be included in the variable's value"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/hotels/{hotelId}/booking/{bookingId}"
			}
			val view.r(hotelId, bookingId) = "/hotels/5/booking/10.html"
			hotelId must equal("5")
			bookingId must equal("10")
		}
		
		it("must not return .html even when its an extension of a one variable url -- edge case"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/{bookingId}"
			}
			val view.r(bookingId) = "/foo.html"
			bookingId must equal("foo")
		}
		
		it("must work with a one variable url -- edge case"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/{bookingId}"
			}
			val view.r(bookingId) = "/foo"
			bookingId must equal("foo")
		}
		
		it("must extract the variable names in order w/one variable"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/hotels/{hotelId}"
			}
			List("hotelId") must equal(view.variableNames.toList)
		}
		
		it("must rextract the variable names in order w/multiple variable"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping{
				def mapping = "/hotels/{hotelId}/booking/{bookingId}/customer/{customerId}"
			}
			List("hotelId", "bookingId", "customerId") must equal(view.variableNames.toList)
		}
		
	}
	
	describe("A MappingFilter"){
		
		it("must work... more testing in other areas will be required - those areas where this will actually be used"){
			val view = new TestView(mockRequest, mockResponse) with TemplateMapping with MappingFilter{
				def mapping = "/unimportant"
				def beforeView = true
			}
			
			view.beforeView must be(true)
		}
	}
}