package scales.servlet

import scales.conf.Config
import ScalesFilter._

/**
Loads settings from scales-app/conf/Settings.scala and returns a Config object from settings
**/
trait SettingsLoader{
	
	/**
	A stub class for right now
	**/
	private object StubSettings extends Config{
		override def urlMappings = ("/index.html".r, classOf[Page]) :: Nil
	}
	
	/**
	performs the logic of loading the Config object from scales-app/conf/Settings.scala
	**/
	def settings: Config = StubSettings
}