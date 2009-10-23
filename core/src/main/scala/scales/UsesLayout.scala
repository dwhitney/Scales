package scales

trait UsesLayout[_ <: Layout]{
	def layout(): Class[_]
}