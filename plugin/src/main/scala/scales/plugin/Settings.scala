package scales.plugin

/**
This is scales-app/conf/Settings.scala
**/
object Settings{
	def settings = {
"""package conf

import scales.conf.Config
import scales.{Mapping, View}
import views.Index

object Settings extends Config{

	/*
	examples of URL schemes:
	listed above: """ + "\"\"\"" + """(/|/index.html)""" + "\"\"\"" + """ -- will match / or /index.html
	"/index.html" -- will only match /index.html
	""" + "\"\"\"" + """(/""|/index(\.?.{0,4}))""" + "\"\"\"" + """ -- will match / or /index.html or /index or /index.htm (or any 4 letter extension)
	""" + "\"\"\"" + """^/([^/]*?)/([^/]*?)(\..*)?$""" + "\"\"\"" + """  -- will match /class/id.html or /class/id.html
	*/

	def urlMappings = new Mapping(""" + "\"\"\"" + """(/|/index.html)""" + "\"\"\"" + """, classOf[Index]) :: Nil
}
"""
	}
}