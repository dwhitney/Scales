package scales.plugin

trait WebXML{
	def webxml = {
		<web-app xmlns="http://java.sun.com/xml/ns/javaee"
		         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
		         version="2.5">

			<!-- 
			************************************************************************************************
			************************************************************************************************
			THIS FILE IS AUTOGENERATED, SO ANY CHANGES WILL BE REPLACED UPON THE NEXT INVOCATION OF compile.
			TO CHANGE HOW THE FILE IS AUTOGENERATED OVERRIDE THE webxml METHOD IN THE WebXML TRAIT
			************************************************************************************************
			************************************************************************************************
			-->
				
			<filter>
				<filter-name>scales</filter-name>
				<filter-class>scales.servlet.ScalesFilter</filter-class>
			</filter>

			<filter-mapping>
				<filter-name>scales</filter-name>
				<url-pattern>/*</url-pattern>
				<dispatcher>FORWARD</dispatcher>
				<dispatcher>REQUEST</dispatcher>
			</filter-mapping>

		</web-app>
	}
}