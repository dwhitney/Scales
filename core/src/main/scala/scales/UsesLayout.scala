package scales

trait UsesLayout[V <: Layout]{
	def layout(): Class[V]
}