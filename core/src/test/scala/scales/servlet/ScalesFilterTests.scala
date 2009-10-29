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

class ScalesFilterTests extends Spec with MustMatchers{

	describe("A ScalesFilter"){
		
		it("must find the /test.html mapping when requested, and write the Page"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe

			//setup a test Config
			object TestSettings extends Config{
				override def urlMappings = ("/test.html".r, classOf[Page]) :: Nil
			}
			
			//setup a test settings loader
			trait TestSettingsLoader extends SettingsLoader{
				override def settings = TestSettings
			}
			
			//setup a test PageBuilder
			trait TestPageBuilder extends PageBuilder{
				override def buildPage(mapping: ScalesFilter.URLMapping): Option[NodeSeq] = Some(<test></test>)
			}
			
			//mock java.io.PrintWriter
			val mockPrintWriter = mock(classOf[java.io.PrintWriter])
			
			//setup mocks
			when(mockRequest.getRequestURI).thenReturn("/test.html", null)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockResponse.getWriter).thenReturn(mockPrintWriter)
			
			val filter = new ScalesFilter with TestSettingsLoader with TestPageBuilder
			filter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).getRequestURI
			verify(mockPrintWriter).print(<test></test>)
			verify(mockFilterChain).doFilter(mockRequest, mockResponse)
		}
		
		it("must find the /test.html mapping when requested, and write the Page while extracting the contextPath"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe

			//setup a test Config
			object TestSettings extends Config{
				override def urlMappings = ("/test.html".r, classOf[Page]) :: Nil
			}
			
			//setup a test settings loader
			trait TestSettingsLoader extends SettingsLoader{
				override def settings = TestSettings
			}
			
			//setup a test PageBuilder
			trait TestPageBuilder extends PageBuilder{
				override def buildPage(mapping: ScalesFilter.URLMapping): Option[NodeSeq] = Some(<test></test>)
			}
			
			//mock java.io.PrintWriter
			val mockPrintWriter = mock(classOf[java.io.PrintWriter])
			
			//setup mocks
			when(mockRequest.getRequestURI).thenReturn("/contextPath/test.html", null)
			when(mockRequest.getContextPath).thenReturn("/contextPath")
			when(mockResponse.getWriter).thenReturn(mockPrintWriter)
			
			val filter = new ScalesFilter with TestSettingsLoader with TestPageBuilder
			filter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).getRequestURI
			verify(mockRequest).getContextPath
			verify(mockPrintWriter).print(<test></test>)
			verify(mockFilterChain).doFilter(mockRequest, mockResponse)
		}
		
		it("must not write anything to the response when no page is found"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe

			//setup a test Config
			object TestSettings extends Config{
				override def urlMappings = Nil
			}
			
			//setup a test settings loader
			trait TestSettingsLoader extends SettingsLoader{
				override def settings = TestSettings
			}
			
			//setup a test PageBuilder
			trait TestPageBuilder extends PageBuilder{
				override def buildPage(mapping: ScalesFilter.URLMapping): Option[NodeSeq] = None
			}
			
			//mock java.io.PrintWriter
			val mockPrintWriter = mock(classOf[java.io.PrintWriter])
			
			//setup mocks
			when(mockRequest.getRequestURI).thenReturn("/doesntexist.html", null)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockResponse.getWriter).thenReturn(mockPrintWriter)
			
			val filter = new ScalesFilter with TestSettingsLoader with TestPageBuilder
			filter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).getRequestURI
			verify(mockPrintWriter, never).print(<test></test>)
			verify(mockFilterChain).doFilter(mockRequest, mockResponse)
		}
		
		it("must not write anything to the response if the URL mapping is found, but page can't be built"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe

			//setup a test Config
			object TestSettings extends Config{
				override def urlMappings = ("/test.html".r, classOf[Page]) :: Nil
			}
			
			//setup a test settings loader
			trait TestSettingsLoader extends SettingsLoader{
				override def settings = TestSettings
			}
			
			//setup a test PageBuilder
			trait TestPageBuilder extends PageBuilder{
				override def buildPage(mapping: ScalesFilter.URLMapping): Option[NodeSeq] = None
			}
			
			//mock java.io.PrintWriter
			val mockPrintWriter = mock(classOf[java.io.PrintWriter])
			
			//setup mocks
			when(mockRequest.getRequestURI).thenReturn("/test.html", null)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockResponse.getWriter).thenReturn(mockPrintWriter)
			
			val filter = new ScalesFilter with TestSettingsLoader with TestPageBuilder
			filter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).getRequestURI
			verify(mockPrintWriter, never).print(<test></test>)
			verify(mockFilterChain).doFilter(mockRequest, mockResponse)
		}
	}
	
	//creates a bunck of mock objects
	private def mockMe = (mock(classOf[HttpServletRequest]), mock(classOf[HttpServletResponse]), mock(classOf[FilterChain]), mock(classOf[FilterConfig]), mock(classOf[ServletContext]), mock(classOf[RequestDispatcher]))
}