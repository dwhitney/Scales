package scales

import scales.xml.ScalesHandler

import java.io.ByteArrayInputStream
import scala.xml.{NodeSeq, Node, XML}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.xml.sax.helpers.{DefaultHandler, XMLReaderFactory}
import org.xml.sax.{InputSource, Attributes}
import scala.collection.mutable.Stack

import java.util.regex.{Pattern, Matcher}


trait Layout extends Page{
	def layoutPage(request: HttpServletRequest, response: HttpServletResponse, page: Page): NodeSeq = {
		val pageXML = page(request, response)
		val layoutXML = this(request, response)
		var nodeMap = Map[String, NodeSeq]()
		(layoutXML \\ "layout").foreach{node =>
			val name = (node \ "@node").toString
			nodeMap = nodeMap + (name -> (pageXML \\ name))
			()
		}
		XML.loadString(parseLayout(layoutXML, nodeMap).toString)
	}
	
	private def parseLayout(xml: NodeSeq, nodeMap: Map[String, NodeSeq]): StringBuffer = {
		val reader = XMLReaderFactory.createXMLReader()
		val layoutHandler = new LayoutHandler(nodeMap)
		reader.setContentHandler(layoutHandler)
		reader.parse(new InputSource(new ByteArrayInputStream(xml.toString.getBytes)))
		layoutHandler.buffer
	}
	
	
	private class LayoutHandler(nodeMap: Map[String, NodeSeq]) extends ScalesHandler{
		
		override def startElement(uri: String, name: String, qName: String, attributes: Attributes) = {
			(uri, name) match {
				case ("http://www.scales.com", "layout") => {
					var node = nodeMap(attributes.getValue("node")).toString
					val childrenOnly = attributes.getValue("childrenOnly")
					if(childrenOnly == "true"){
						val pattern = Pattern.compile("^<.*?>(.*)</.*>$", Pattern.DOTALL | Pattern.MULTILINE)
						val matcher = pattern.matcher(node)
						if(matcher.matches()){
							node = matcher.group(1)
						}
					}
					
					buffer.append(node)
				}
				case _ => addToBuffer(uri, name, qName, attributes)
			}
		}

		override def endElement(uri: String, localName: String, qName: String) = {
			(uri, localName) match{
				case ("http://www.scales.com", "layout") => ()
				case _ => buffer.append("</" + qName + ">")
			}
		}
					
	}
}
