package scales.xml

import java.io.{FileInputStream, BufferedReader, InputStreamReader, File}
import org.xml.sax.helpers.{DefaultHandler, XMLReaderFactory}
import org.xml.sax.{InputSource, Attributes, SAXParseException}
import scala.collection.mutable.Stack
import java.io.{ByteArrayInputStream}

/**
Scales Handler Singleton that performs the parsing of a document via SAX.
**/
object ScalesHandler{
	
	/**
	parses the given string.  If it gets an error for the non-existing "s" namespace, it'll add the namespace and try again
	**/
	def parse(xml: String): String = {	
		val string = try{
			doParse(xml)
		}catch{
			case e: SAXParseException => {
				//wrap the erroring XML in the ignore tags, and it may parse
				doParse("<s:ignore xmlns:s='http://www.scales.com'>" + xml + "</s:ignore>")
			}
		}
		postProcess(string)
	}
	
	/**
	parses the given file.  If it gets an error for the non-existing "s" namespace, it'll add the namespace and try again
	**/
	def parse(file: File): String = {
		val xml = try{
			doParse(new FileInputStream(file))
		}catch{
			case e: SAXParseException => {
				//wrap the erroring XML in ignore tags by reading the lines from the file and creating a string
				val buffer = new StringBuffer()
				buffer.append("<s:ignore xmlns:s='http://www.scales.com'>")
				val myInput = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				var thisLine = ""
				while (thisLine != null) {  
					buffer.append(thisLine + "\n")
					thisLine = myInput.readLine()
				}
				buffer.append("</s:ignore>")
				doParse(buffer.toString())
			}
		}
		postProcess(xml)
	}
	
	/**
		does some reformatting after the html has been parsed.
	**/
	private def postProcess(xml: String): String = {
		xml
		.replaceAll("\\{", "\\{\\{")
		.replaceAll("\\}", "\\}\\}")
		.replaceAll("<!-- @@@@---@@@@ SCALA CODE BEGINS HERE @@@@---@@@@ -->", "{")
		.replaceAll("<!-- @@@@---@@@@ SCALA CODE ENDS HERE @@@@---@@@@ -->", "}")
	}
	
	/**
		performs the actual parsing of the input stream
	**/
	private def doParse(fileInputStream: FileInputStream): String = {
		val reader = XMLReaderFactory.createXMLReader()
		val scalesHandler = new ScalesHandler()
		reader.setContentHandler(scalesHandler)
		reader.parse(new InputSource(fileInputStream))
		scalesHandler.buffer.toString
	}
	
	/**
		performs the actual parsing of the string
	**/
	private def doParse(xml: String): String = {
		val reader = XMLReaderFactory.createXMLReader()
		val scalesHandler = new ScalesHandler()
		reader.setContentHandler(scalesHandler)
		reader.setErrorHandler(scalesHandler)
		reader.parse(new InputSource(new ByteArrayInputStream(xml.getBytes)))
		scalesHandler.buffer.toString
	}
}

/**
This class is a Handler for a SAX Parser.  The most significant thing it does is look for scales tags for replacing
**/
class ScalesHandler extends DefaultHandler{
	
	val buffer = new StringBuffer()
	private var urls = List[String]()
	private var namespaceStack = new Stack[(String, String)]()
	private var tagStack = new Stack[(String, String, Attributes)]()
	
	/**
	looks for specific scales tags to process - also appends normal tags to the buffer.
	**/
	override def startElement(uri: String, name: String, qName: String, attributes: Attributes) = {
		(uri, name) match {
			case ("http://www.scales.com", "component") => { 
				appendCodeBegins()
				tagStack.push((uri, name, attributes)) 
			}
			case ("http://www.scales.com", "ignore") => ()
			case ("http://www.scales.com", "code") => appendCodeBegins()
			case _ => addToBuffer(uri, name, qName, attributes)
		}
		
	}
	
	/**
	looks out for scales tags for processing, and appends other tags to the buffer
	**/
	override def endElement(uri: String, localName: String, qName: String): Unit = {

		(uri, localName) match {
			case ("http://www.scales.com", "ignore") => ()
			case ("http://www.scales.com", "code") => appendCodeEnds
			case ("http://www.scales.com", "component") => {
				val (u, n, att) = tagStack.pop
				buffer.append(att.getValue(0) + "(request, response)")
				appendCodeEnds()
			}
			case _ => buffer.append("</" + qName + ">")
		}
		()
	}
	
	/**
	this replaces <s:code> tags with the junk below.  The junk below is post-processed to become code inside of a NodeSeq
	**/
	private def appendCodeBegins(){
		buffer.append("<!-- @@@@---@@@@ SCALA CODE BEGINS HERE @@@@---@@@@ -->")
	}
	
	/**
	this replaces </s:code> tags with the junk below.  The junk below is post-processed to become code inside of a NodeSeq
	**/
	private def appendCodeEnds(){
		buffer.append("<!-- @@@@---@@@@ SCALA CODE ENDS HERE @@@@---@@@@ -->")
	}
	
	/**
	very basic prefix mapping - just puts the prefix in place
	**/
	override def startPrefixMapping(prefix: String, uri: String) = {
		namespaceStack.push((prefix, uri))
	}
	
	/**
	all characters are appended to the buffer
	**/
	override def characters(chars: Array[Char], start: Int, length: Int){
		buffer.append(chars, start, length)
	}
	
	/**
	adds an element to the buffer
	**/
	protected def addToBuffer(uri: String, name: String, qName: String, attributes: Attributes) = {
		buffer.append("<" + qName)
		if(namespaceStack.length > 0){
			while(namespaceStack.length != 0){
				buffer.append(" ")
				val (prefix, uri) = namespaceStack.pop
				var xmlns = "xmlns"
				if(prefix != "") xmlns = xmlns + ":"
				buffer.append(xmlns + prefix + "=\"" + uri + "\"")
			}

		}
		
		for(i <- 0 until attributes.getLength){
			buffer.append(" ")
			buffer.append(attributes.getQName(i) + "=\"")
			buffer.append(attributes.getValue(i) + "\"")
		}
		
		buffer.append(">")
	}
}