package scales.sitemesh

import com.opensymphony.module.sitemesh.{Config, Decorator, DecoratorMapper, Page}
import com.opensymphony.module.sitemesh.mapper.{AbstractDecoratorMapper,DefaultDecorator}
import java.util.Properties
import javax.servlet.http.HttpServletRequest
import SiteMesh._
import scales.servlet.SettingsLoader

class ScalesDecoratorMapper extends AbstractDecoratorMapper with DecoratorMapper with SettingsLoader{
	
	override def init(config: Config, properties: Properties, parent: DecoratorMapper): Unit = {
		super.init(config, properties, parent)
	}
	
	override def getDecorator(request: HttpServletRequest, page: Page): Decorator = {
		val siteMeshDecorator = request.getAttribute(SiteMesh.SITE_MESH_DECORATOR)
		def decoratorsDir = callMethod[String]("decoratorsDir").get
		if(siteMeshDecorator != null){
			new DefaultDecorator(siteMeshDecorator.toString, decoratorsDir + siteMeshDecorator.toString, new java.util.HashMap[Any, Any]())
		}else{
			null
		}
	}
	
}