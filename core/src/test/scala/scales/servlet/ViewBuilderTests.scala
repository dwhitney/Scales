package scales.servlet

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.stubbing.OngoingStubbing

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, FilterConfig, ServletContext, RequestDispatcher}

import scales.conf.Config
import ScalesFilter._
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
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestView])) must equal(Some("Test!"))
		}
		
		it("must filter out a GET only view when the method isn't GET"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("POST")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestGETView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a GET only view when the method is GET"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestGETView])) must equal(Some("Test!"))
		}
		
		it("must filter out a POST only view when the method isn't POST"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestPOSTView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a POST only view when the method is POST"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("POST")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestPOSTView])) must equal(Some("Test!"))
		}
		
		it("must filter out a PUT only view when the method isn't PUT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestPUTView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a PUT only view when the method is PUT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("PUT")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestPUTView])) must equal(Some("Test!"))
		}
		
		it("must filter out a DELETE only view when the method isn't DELETE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestDELETEView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a DELETE only view when the method is DELETE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("DELETE")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestDELETEView])) must equal(Some("Test!"))
		}
		
		it("must filter out a HEAD only view when the method isn't HEAD"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestHEADView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a HEAD only view when the method is HEAD"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("HEAD")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestHEADView])) must equal(Some("Test!"))
		}
		
		it("must filter out a OPTIONS only view when the method isn't OPTIONS"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestOPTIONSView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a OPTIONS only view when the method is OPTIONS"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("OPTIONS")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestOPTIONSView])) must equal(Some("Test!"))
		}
		
		it("must filter out a TRACE only view when the method isn't TRACE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestTRACEView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a TRACE only view when the method is TRACE"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("TRACE")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestTRACEView])) must equal(Some("Test!"))
		}
		
		it("must filter out a CONNECT only view when the method isn't CONNECT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestCONNECTView])) must equal(None)
			verify(mockResponse).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
		}
		
		it("must not filter out a CONNECT only view when the method is CONNECT"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("CONNECT")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestCONNECTView])) must equal(Some("Test!"))
		}
		
		it("must responsd to multiple methods if they are used"){
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			
			when(mockRequest.getMethod).thenReturn("GET", "POST")
			
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestGETAndPOSTView])) must equal(Some("Test!"))
			TestThing.buildView(mockRequest, mockResponse, (null, classOf[TestGETAndPOSTView])) must equal(Some("Test!"))
		}
		
	}
	
}