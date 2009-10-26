package scales.conf

trait Config{
	def urlMappings: List[(scala.util.matching.Regex, Class[P] forSome {type P <: Page})]
	// = ("""^/([^/]*?)/([^/]*?)(\..*)?$""".r, classOf[Page]) :: Nil
	def pagePackage: String = "pages"
	def layoutsPackage: String = "layouts"
	def componentsPackage: String = "components"
}