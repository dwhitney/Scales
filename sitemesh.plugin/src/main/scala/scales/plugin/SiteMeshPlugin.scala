package scales.plugin

import scala.xml._
import scala.xml.transform._
import scales.servlet.WebXML
import sbt._

trait SiteMeshPlugin extends DefaultWebProject with WebXML{
	def scalesAppDirSM = path("src") / "main" / "scala" / "scales-app"
	def webAppDirSM = path("src") / "main" / "webapp"

	override def webxml = { 
		val addSiteMeshToWebXML = new RuleTransformer(new RewriteRule{
			override def transform(node: Node) = node match{
				case <web-app>{nodes @ _*}</web-app> =>
					<web-app xmlns="http://java.sun.com/xml/ns/javaee"
					         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
					         version="2.5">
					{nodes ++ 
						<filter>
						    <filter-name>sitemesh</filter-name>
						    <filter-class>com.opensymphony.sitemesh.webapp.SiteMeshFilter</filter-class>
						</filter>
						<filter-mapping>
						    <filter-name>sitemesh</filter-name>
						    <url-pattern>/*</url-pattern> <!-- */ this comment is here only to make this less ugly in TextMate -->
							<dispatcher>FORWARD</dispatcher>
						</filter-mapping>
					}</web-app>
				case node => super.transform(node)
			}
		})
		//*/ this comment is here only to make this less ugly in TextMate
		addSiteMeshToWebXML(super.webxml)
	}
	def siteMeshXML = {
<sitemesh>
    <page-parsers>
        <parser content-type="text/html" class="com.opensymphony.module.sitemesh.parser.HTMLPageParser" />
        <parser content-type="text/html;charset=ISO-8859-1" class="com.opensymphony.module.sitemesh.parser.HTMLPageParser" />
    </page-parsers>
    <decorator-mappers>
		<mapper class="scales.sitemesh.ScalesDecoratorMapper"/>
    </decorator-mappers>
</sitemesh>
	}

	def createSiteMeshXMLAction = task{
		(webAppDirSM / "WEB-INF").asFile.mkdirs
		val file = (webAppDirSM / "WEB-INF" / "sitemesh.xml").asFile
		file.delete
		FileUtilities.append((webAppDirSM / "WEB-INF" / "sitemesh.xml").asFile, siteMeshXML.toString, log)
		None
	}


	def decorator = {
		"""<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
	<head>
		<title><decorator:title default="Hello, World!"/></title>
		<style type="text/css">
			html{
				background:#808000;
			}
		</style>
		<decorator:head/>
	</head>
	<body>
		<decorator:body/>
	</body>
</html>""" //"this comment is only here to deal with a TextMate bug
	}

	def sampleSiteMeshPage = {
		"""<html>
	<head>
		<title>Sample SiteMesh Page</title>
	</head>
	<body>
		<h1>${model.hello}</h1>
	</body>
</html>
		"""
	}

	def siteMeshView = {
"""package views

import scales.View
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scala.reflect.BeanProperty
import scales.sitemesh.SiteMesh

class Index(request: HttpServletRequest, response: HttpServletResponse) extends View(request, response) with SiteMesh{
	def jsp = "sample"
	override def decorator = "main"

	@BeanProperty
	val hello = "Hello, World"

}"""
	}

	def createJspAndDecorators = task{
		val file = (webAppDirSM / "WEB-INF" / "jsp" / "decorators" / "main.jsp").asFile
		file.delete
		FileUtilities.append((webAppDirSM / "WEB-INF" / "jsp" / "decorators" / "main.jsp").asFile, decorator, log)

		val sample = (webAppDirSM / "WEB-INF" / "jsp" / "sample.jsp").asFile
		sample.delete
		FileUtilities.append((webAppDirSM / "WEB-INF" / "jsp" / "sample.jsp").asFile, sampleSiteMeshPage, log)

		val siteMeshSampleView = (scalesAppDirSM / "views" / "Index.scala").asFile
		siteMeshSampleView.delete
		FileUtilities.append((scalesAppDirSM / "views" / "Index.scala").asFile, siteMeshView, log)
		None
	}

	lazy val createSiteMeshXml = {createSiteMeshXMLAction}
	lazy val initSiteMesh = {createJspAndDecorators } dependsOn(createSiteMeshXml)


	override def compileAction = task{ super.compileAction.run }
}