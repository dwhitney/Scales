package scales.servlet

import scala.xml.Node

trait WebXML{
	def webxml: Node = {
		<web-app xmlns="http://java.sun.com/xml/ns/javaee"
		         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
		         version="2.5">

			<!-- 
			**************************************************************************************************
			**************************************************************************************************
			THIS FILE IS AUTO-GENERATED, SO ANY CHANGES WILL BE REPLACED UPON THE NEXT INVOCATION OF compile.
			TO CHANGE HOW THE FILE IS AUTO-GENERATED OVERRIDE THE webxml METHOD IN THE WebXML TRAIT
			YOU CAN ALSO SHUT OFF THE GENERATION BY ADDING override def createWebXMLAction = task{ None } TO
			YOUR PROJECT DEFINITION FILE
			**************************************************************************************************
			**************************************************************************************************
			-->
				
			<filter>
				<filter-name>scales</filter-name>
				<filter-class>scales.servlet.ScalesFilter</filter-class>
			</filter>

			<filter-mapping>
				<filter-name>scales</filter-name>
				<url-pattern>/*</url-pattern><!-- */ this comment is here only to make this less ugly in TextMate -->
			</filter-mapping>

		</web-app>
	}
}