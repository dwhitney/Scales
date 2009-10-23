package scales.servlet

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.stubbing.OngoingStubbing

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, FilterConfig, ServletContext, RequestDispatcher}

class ScalesUrlMappingFilterTests extends Spec with MustMatchers{

	describe("A ScalesUrlMappingFilter"){
		
		it("must set an attribute of ScalesUrlMappingFilter.CALLED if it is not already set"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe
			
			when(mockFilterConfig.getServletContext()).thenReturn(mockServletContext)
			when(mockServletContext.getRequestDispatcher("/scales.dispatch")).thenReturn(mockRequestDispatcher)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockRequest.getRequestURI).thenReturn("/hello-world.html", null)
			
			val scalesFilter = new ScalesUrlMappingFilter()
			scalesFilter.init(mockFilterConfig)
			scalesFilter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).setAttribute(ScalesUrlMappingFilter.CALLED, ScalesUrlMappingFilter.CALLED)
		}
		
		it("must move on in the filter chain if ScalesUrlMappingFilter.CALLED has already been set (i.e. this filter should only be called once per request)"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe
						
			when(mockRequest.getAttribute(ScalesUrlMappingFilter.CALLED)).thenReturn(ScalesUrlMappingFilter.CALLED, null)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockRequest.getRequestURI).thenReturn("/hello-world.html", null)
			val scalesFilter = new ScalesUrlMappingFilter()
			scalesFilter.init(mockFilterConfig)
			scalesFilter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).getAttribute(ScalesUrlMappingFilter.CALLED)
			verify(mockFilterChain).doFilter(mockRequest, mockResponse)
			
		}
		
		it("must send properly mapped URL to the servlet for processing"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe
			
			when(mockFilterConfig.getServletContext).thenReturn(mockServletContext)
			when(mockServletContext.getRequestDispatcher("/scales.dispatch")).thenReturn(mockRequestDispatcher)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockRequest.getRequestURI).thenReturn("/hello-world.html", null)
					
			val scalesFilter = new ScalesUrlMappingFilter()
			scalesFilter.init(mockFilterConfig)
			scalesFilter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequestDispatcher).include(mockRequest, mockResponse)		
			verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse)
		}
		
		it("must continue along the filter chain when no url is found in the servlet"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe
			
			when(mockFilterConfig.getServletContext).thenReturn(mockServletContext)
			when(mockServletContext.getRequestDispatcher("/scales.dispatch")).thenReturn(mockRequestDispatcher)
			when(mockRequest.getAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND)).thenReturn(ScalesUrlMappingFilter.URL_NOT_FOUND, null)
			when(mockRequest.getContextPath).thenReturn("", null)
			when(mockRequest.getRequestURI).thenReturn("/hello-world.html", null)
					
			val scalesFilter = new ScalesUrlMappingFilter()
			scalesFilter.init(mockFilterConfig)
			scalesFilter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequestDispatcher).include(mockRequest, mockResponse)
			verify(mockFilterChain).doFilter(mockRequest, mockResponse)
		}
		
		it("must remove the context path from the requestURI before passing to the servlet"){
			val(mockRequest, mockResponse, mockFilterChain, mockFilterConfig, mockServletContext, mockRequestDispatcher) = mockMe
			
			when(mockFilterConfig.getServletContext).thenReturn(mockServletContext)
			when(mockServletContext.getRequestDispatcher("/scales.dispatch")).thenReturn(mockRequestDispatcher)
			when(mockRequest.getAttribute(ScalesUrlMappingFilter.URL_NOT_FOUND)).thenReturn(ScalesUrlMappingFilter.URL_NOT_FOUND, null)
			when(mockRequest.getContextPath).thenReturn("/scales", null)
			when(mockRequest.getRequestURI).thenReturn("/scales/hello-world.html", null)
					
			val scalesFilter = new ScalesUrlMappingFilter()
			scalesFilter.init(mockFilterConfig)
			scalesFilter.doFilter(mockRequest, mockResponse, mockFilterChain)
			
			verify(mockRequest).setAttribute(ScalesUrlMappingFilter.REQUEST_URI, "/hello-world.html")
		}
	}
	
	private def mockMe = (mock(classOf[HttpServletRequest]), mock(classOf[HttpServletResponse]), mock(classOf[FilterChain]), mock(classOf[FilterConfig]), mock(classOf[ServletContext]), mock(classOf[RequestDispatcher]))
	
}