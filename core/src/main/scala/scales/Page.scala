package scales

import scala.util.matching.Regex

trait Page extends View{
	var UrlVars: Regex = """^/([^/]*?)/([^/]*?)(\..*)?$""".r //  use as: val UrlVars(clazz, id, extension) = "/class/id.extension"
}
