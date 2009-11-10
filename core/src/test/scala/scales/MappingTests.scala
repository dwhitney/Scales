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

	describe("A Mapping"){
			
		it("must return a regex and we'll check to see if it works"){
			
			def mapping = new Mapping("""^/([^/]*?)/([^/]*?)(\..*)?$""", classOf[View])
			
			val mapping.r(one, two, three) = "/one/two.three"
			one must equal("one")
			two must equal("two")
			three must equal(".three")
		}		
	}
	
	describe("A TemplateMapping"){
		
		it("must turn a template like /hotels/{hotelId} into a usable regex"){
			val mapping = new TemplateMapping("/hotels/{hotelId}", classOf[View])
			val mapping.r(hotelId) = "/hotels/5"
			hotelId must equal("5")
			()
		}
		
		it("must turn a template like /hotels/{hotelId}/booking/{bookingId}/foo into a usable regex"){
			val mapping = new TemplateMapping("/hotels/{hotelId}/booking/{bookingId}/foo", classOf[View])
			val mapping.r(hotelId, bookingId) = "/hotels/5/booking/10/foo"
			hotelId must equal("5")
			bookingId must equal("10")
		}
		
		it("must turn a template like /hotels/{hotelId}/booking/{bookingId} into a usable regex"){
			val mapping = new TemplateMapping("/hotels/{hotelId}/booking/{bookingId}", classOf[View])
			val mapping.r(hotelId, bookingId) = "/hotels/5/booking/10"
			hotelId must equal("5")
			bookingId must equal("10")
		}
		
		it("must turn a template into vars even if the url contains an extension.  That extension should not be included in the variable's value"){
			val mapping = new TemplateMapping("/hotels/{hotelId}/booking/{bookingId}", classOf[View])
			val mapping.r(hotelId, bookingId) = "/hotels/5/booking/10.html"
			hotelId must equal("5")
			bookingId must equal("10")
		}
		
		it("must not return .html even when its an extension of a one variable url -- edge case"){
			val mapping = new TemplateMapping("/{bookingId}", classOf[View])
			
			val mapping.r(bookingId) = "/foo.html"
			bookingId must equal("foo")
		}
		
		it("must work with a one variable url -- edge case"){
			val mapping = new TemplateMapping("/{bookingId}", classOf[View])
			val mapping.r(bookingId) = "/foo"
			bookingId must equal("foo")
		}
		
		it("must extract the variable names in order w/one variable"){
			val mapping = new TemplateMapping("/hotels/{hotelId}", classOf[View])
			List("hotelId") must equal(mapping.variableNames.toList)
		}
		
		it("must rextract the variable names in order w/multiple variable"){
			val mapping = new TemplateMapping("/hotels/{hotelId}/booking/{bookingId}/customer/{customerId}", classOf[View])
			List("hotelId", "bookingId", "customerId") must equal(mapping.variableNames.toList)
		}
		
	}
	
	describe("A MappingFilter"){
		
		it("must work with the Mapping class... more testing in other areas will be required - those areas where this will actually be used"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			val mapping = new Mapping("/unimportant", classOf[View]) with MappingFilter{
				def beforeView(request: HttpServletRequest, response: HttpServletResponse) = true
			
			}
						
			mapping.beforeView(mockRequest, mockResponse) must be(true)
		}
		
		it("must work with the TemplateMapping class... more testing in other areas will be required - those areas where this will actually be used"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			val mapping = new TemplateMapping("/unimportant", classOf[View]) with MappingFilter{
				def beforeView(request: HttpServletRequest, response: HttpServletResponse) = true
			
			}
						
			mapping.beforeView(mockRequest, mockResponse) must be(true)
		}
	}
}