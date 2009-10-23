package scales.conf

object Config{
	def mappings = ("""^/([^/]*?)/([^/]*?)(\..*)?$""".r, classOf[Page]) :: Nil
	def pagePackage = "pages"
}