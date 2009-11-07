import sbt._

class ScalesCoreProject(info: ProjectInfo) extends DefaultProject(info){

	val scalaTest = "org.scalatest" % "scalatest" % "0.9.5" % "test"
	val mockito = "org.mockito" % "mockito-all" % "1.8.0" % "test"
	val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"

	val repo1 = "maven-repo" at "http://repo2.maven.org/maven2/"
	
	
	override def mainCompileConfiguration = {
		val superCompileConfiguration = super.mainCompileConfiguration
		new MainCompileConfig {
			override def testDefinitionClassNames: Iterable[String] = "scales.TemplateMapping" :: "scales.Mapping" :: super.testDefinitionClassNames.toList
		}
	}
	
	lazy val tmp = task {
		mainCompileConditional.analysis.allTests foreach {
			case TestDefinition(isModule, className, superClassName) if(superClassName == "scales.Mapping" || superClassName == "scales.TemplateMapping") => println(className)
		}
		None
	} dependsOn(compile)
	
	
	lazy val mappingAction = task{ 
		val classLoader = ClasspathUtilities.toLoader(path("target") / "classes")
		//clean up classes so they can be loaded
		val classNames = mainCompileConditional.analysis.allProducts.map{ path: Path =>
			path.toString.replaceFirst("^.*?classes/", "").replaceFirst("\\.class$", "").replaceAll("/", ".")
		}
		
		var mappedViews: Set[String] = Set[String]()
		//loop through class names and try to load them.
		classNames.foreach{ s: String =>
			try{
				val clazz = classLoader.loadClass(s.toString)
				//now laod the interfaces and see if any of them are "scales.Mapping"
				//if they are then add them to the mapped views set
				clazz.getInterfaces.foreach{c: Class[_] =>
					c.getName match {
						case "scales.Mapping" if(!clazz.isInterface) => mappedViews = mappedViews + s
						case "scales.TemplateMapping" if(!clazz.isInterface) => mappedViews = mappedViews + s
						case _ => ()
					}
				}
			}catch{
				case notFound: NoClassDefFoundError => { 
					// letting this fail silently for now - seems that it only happens when the the class has a dependency
					//on an outside jar, which shouldn't ben an issue in this particular case
				}
			}
		}
		val str = mappedViews.mkString("[", "," ,"]")
		println(str)
		None
	} dependsOn(compileAction)
		
	
}