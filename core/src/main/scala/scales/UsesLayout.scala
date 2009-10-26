package scales

import scala.reflect.Manifest

trait UsesLayout[L <: Layout]{
	def layout: Class[L]
}