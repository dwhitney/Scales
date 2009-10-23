package scales.plugin

import sbt._

trait ScalesPlugin extends Project{
	lazy val hello = task { log.info("Hello, World!"); None }
}