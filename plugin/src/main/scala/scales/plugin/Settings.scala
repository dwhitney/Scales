package scales.plugin

/**
This is scales-app/conf/Settings.scala
**/
object Settings{
	def settings = {
"""package conf

import _root_.scales.Page
import _root_.scales.conf.Config
import _root_.pages._
import _root_.scala.util.matching.Regex

object Settings extends Config{
	/*
	This type is a tuple with the first element being a regular expression extractor 
	that matches a URL and the second element being any class that extends the Page trait.
	An instance of the class will be created and invoked when the URL of a given request  
	matches the regular expression
	*/
	type URLMapping = (Regex, Class[P] forSome {type P <: Page})

	/*
	examples of URL schemes:
	listed above: """ + "\"\"\"" + """(/""|/index.html)""" + "\"\"\"" + """.r -- will match / or /index.html
	"/index.html".r -- will only match /index.html
	""" + "\"\"\"" + """(/""|/index(\.?.{0,4}))""" + "\"\"\"" + """.r -- will match / or /index.html or /index or /index.htm (or any 4 letter extension)
	""" + "\"\"\"" + """^/([^/]*?)/([^/]*?)(\..*)?$""" + "\"\"\"" + """.r  -- will match /class/id.html or /class/id.html
	*/

	def urlMappings: List[URLMapping] = (""" + "\"\"\"" + """(/""|/index.html)""" + "\"\"\"" + """.r, classOf[Index]) :: ("/ajax.html".r, classOf[Ajax]) :: Nil
}
"""
	}
}