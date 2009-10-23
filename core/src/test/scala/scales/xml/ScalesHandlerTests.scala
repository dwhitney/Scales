package scales.xml

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._

import java.io.{ByteArrayInputStream}
import org.xml.sax.helpers.{DefaultHandler, XMLReaderFactory}
import org.xml.sax.{InputSource, Attributes}
import scala.collection.mutable.Stack

class ScalesHandlerTests extends Spec with MustMatchers{
	
	describe("A ScalesHandler"){
		
		it("must parse a document and replace brackets with double brackets"){
			
			val xml = """
				<blah>
				{
					import scala.io.Source
				}
				</blah>
			"""
			
			val xml2 = """
				<blah>
				{{
					import scala.io.Source
				}}
				</blah>
			"""
			
			val buffer = ScalesHandler.parse(xml)
			buffer.toString.trim() must equal(xml2.trim())
		}
		
		it("must parse component tags"){
			val xml = """
				<blah xmlns:s='http://www.scales.com'>
					<s:component class='foo.Bar'/>
				</blah>
			"""
			val buffer = ScalesHandler.parse(xml)
			
			val afterParse = """
				<blah xmlns:s="http://www.scales.com">
					{foo.Bar(request, response)}
				</blah>
			"""
			//"
			buffer.toString.trim() must equal(afterParse.trim())
		}
		
		it("must ignore the <s:ignore xmlns:s='http://www.scales.com'></s:ignore> tag"){
			val xml = """
				<s:ignore xmlns:s="http://www.scales.com">
					<h1>Hello, World!</h1>
				</s:ignore>
			"""
			//"
			val buffer = ScalesHandler.parse(xml)
			buffer.toString().trim() must equal("<h1 xmlns:s=\"http://www.scales.com\">Hello, World!</h1>")
		}
	
		it("must parse even if the correct namespace for components isn't declared"){
			val xml = "<html xmlns:s=\"http://www.scales.com\"><body><s:component class=\"foo.Bar\"/></body></html>"
			val buffer = ScalesHandler.parse(xml)
			buffer.toString.trim() must equal("<html xmlns:s=\"http://www.scales.com\"><body>{foo.Bar(request, response)}</body></html>")
		}
	
		it("must parse and produce a NodeSeq"){
			/*
				the SAX parser won't parse a NodeSeq
				<head></head>
				<body></body>
				But NodeSeqs are very useful, so I want to produce them 
			*/
			val nodeSeq = """<head></head><body></body>"""
			val buffer = ScalesHandler.parse(nodeSeq)
			buffer.toString.trim() must equal("<head xmlns:s=\"http://www.scales.com\"></head><body></body>")
		}
		
		it("must replace <s:code/> with {} "){
			/*
				the SAX parser won't parse a NodeSeq
				<head></head>
				<body></body>
				But NodeSeqs are very useful, so I want to produce them 
			*/
			val nodeSeq = """<head><s:code>4 + 4</s:code></head>"""
			val buffer = ScalesHandler.parse(nodeSeq)
			buffer.toString.trim() must equal("<head xmlns:s=\"http://www.scales.com\">{4 + 4}</head>")
		}
	
	}
}