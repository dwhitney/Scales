package scales.servlet

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.stubbing.OngoingStubbing

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, FilterConfig, ServletContext, RequestDispatcher}

import scales.conf.Config
import scala.xml.NodeSeq
import scales._


object TestThing extends ViewBuilder{}

/**
This is just for testing in the ViewBuilderTests
**/
class TestView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response){
	override def apply() = "Test!"
}

class TestGETView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = GET :: Nil
	override def apply() = "Test!"
}

class TestPOSTView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = POST :: Nil
	override def apply() = "Test!"
}

class TestPUTView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = PUT :: Nil
	override def apply() = "Test!"
}

class TestDELETEView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = DELETE :: Nil
	override def apply() = "Test!"
}

class TestHEADView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = HEAD :: Nil
	override def apply() = "Test!"
}

class TestOPTIONSView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = OPTIONS :: Nil
	override def apply() = "Test!"
}

class TestTRACEView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = TRACE :: Nil
	override def apply() = "Test!"
}

class TestCONNECTView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = CONNECT :: Nil
	override def apply() = "Test!"
}

class TestGETAndPOSTView(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with REST{
	override def methods = GET :: POST :: Nil
	override def apply() = "Test!"
}

class ViewBuilderTests extends Spec with MustMatchers{
	
	describe("A ViewBuilder"){
		
		it("must build a view"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			when(mockRequest.getMethod).thenReturn("POST")
			
			object TestThing extends ViewBuilder{}
			val mapping = new Mapping("/blah", classOf[TestView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must set the mapping in the request"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			when(mockRequest.getMethod).thenReturn("POST")
			
			object TestThing extends ViewBuilder{}
			val mapping = new Mapping("/blah", classOf[TestView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping)
			verify(mockRequest).setAttribute("mapping", mapping)
		}
		
		it("must build a view with a TemplateMapping"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			when(mockRequest.getMethod).thenReturn("POST")
			
			object TestThing extends ViewBuilder{}
			val mapping = new TemplateMapping("/blah", classOf[TestView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a GET only view when the method isn't GET"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("POST")
			val mapping = new Mapping("/test", classOf[TestGETView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a GET only view when the method is GET"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestGETView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a POST only view when the method isn't POST"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestPOSTView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a POST only view when the method is POST"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("POST")
			val mapping = new Mapping("/test", classOf[TestPOSTView])
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a PUT only view when the method isn't PUT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestPUTView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a PUT only view when the method is PUT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("PUT")
			val mapping = new Mapping("/test", classOf[TestPUTView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a DELETE only view when the method isn't DELETE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestDELETEView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a DELETE only view when the method is DELETE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("DELETE")
			val mapping = new Mapping("/test", classOf[TestDELETEView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a HEAD only view when the method isn't HEAD"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestHEADView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a HEAD only view when the method is HEAD"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("HEAD")
			val mapping = new Mapping("/test", classOf[TestHEADView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a OPTIONS only view when the method isn't OPTIONS"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestOPTIONSView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a OPTIONS only view when the method is OPTIONS"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("OPTIONS")
			val mapping = new Mapping("/test", classOf[TestOPTIONSView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a TRACE only view when the method isn't TRACE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestTRACEView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a TRACE only view when the method is TRACE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("TRACE")
			val mapping = new Mapping("/test", classOf[TestTRACEView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must filter out a CONNECT only view when the method isn't CONNECT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			val mapping = new Mapping("/test", classOf[TestCONNECTView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a CONNECT only view when the method is CONNECT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("CONNECT")
			val mapping = new Mapping("/test", classOf[TestCONNECTView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}
		
		it("must responsd to multiple methods if they are used"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET", "POST")
			val mapping = new Mapping("/test", classOf[TestGETAndPOSTView])
			
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
			TestThing.buildView(mockRequest, mockResponse, mapping) must equal(Some("Test!"))
		}

	}
	
}