package scales.servlet

import scales.Page
import ScalesFilter._
import scala.xml.NodeSeq

/**
trait that builds pages.  If the Page is contained by the framework, Some(NodeSeq) of that page will be returned,
otherwise None is returned.  The NodeSeq is the rendered product of the page 
**/
trait PageBuilder{
	
	/**
	performs the logic of building a Page, including processing the layout.
	If the page cannot be built, None is returned, otherwise Some(page.apply()) is returned
	**/
	def buildPage(mapping: URLMapping): Option[NodeSeq] = Some(<page/>)
}