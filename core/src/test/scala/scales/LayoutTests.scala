package scales

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class LayoutTests extends Spec with MustMatchers{
	
	describe("A Layout"){
		
		it("must return a NodeSeq when layoutPage is called"){
			object TmpLayout extends Layout{
				override def apply(request: HttpServletRequest, response: HttpServletResponse) = <h1>Hello, World!</h1>
			}
			
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			val mockPage = mock(classOf[Page])
			when(mockPage.apply(mockRequest, mockResponse)).thenReturn(<h1>Hello, World!</h1>)
			
			TmpLayout.layoutPage(mockRequest, mockResponse, mockPage) must equal(<h1>Hello, World!</h1>)
		}
		
		it("""must replace the <s:layout node="whatever"/> with the content of the page"""){
			class TmpLayout extends Layout{
				override def apply(request: HttpServletRequest, response: HttpServletResponse) = <span xmlns:s="http://www.scales.com"><s:layout node="h1"/></span>
			}
			
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			val mockPage = mock(classOf[Page])
			when(mockPage.apply(mockRequest, mockResponse)).thenReturn(<h1>Hello, World</h1>)
			
			new TmpLayout().layoutPage(mockRequest, mockResponse, mockPage) must equal(<span xmlns:s="http://www.scales.com"><h1>Hello, World</h1></span>)
		}
		
		it("""must replace the <s:layout node="whatever" chidrenOnly="true"/> with only the children of the specified elements"""){
			class TmpLayout extends Layout{
				override def apply(request: HttpServletRequest, response: HttpServletResponse) = <span xmlns:s="http://www.scales.com"><s:layout node="h1" childrenOnly="true"/></span>
			}
			
			val mockRequest = mock(classOf[HttpServletRequest])
			val mockResponse = mock(classOf[HttpServletResponse])
			val mockPage = mock(classOf[Page])
			when(mockPage.apply(mockRequest, mockResponse)).thenReturn(<h1>Hello, World</h1>)
			
			new TmpLayout().layoutPage(mockRequest, mockResponse, mockPage) must equal(<span xmlns:s="http://www.scales.com">Hello, World</span>)
		}
		
	}
	
}