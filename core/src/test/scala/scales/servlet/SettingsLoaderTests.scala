package scales.servlet

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._
import org.mockito.Matchers._

import scales.conf.Config

import scala.tools.nsc._
import scala.tools.nsc.reporters._
import java.io.{File, FileOutputStream}
import java.lang.reflect._
import java.net._

/**
I'm finding it very difficult to dynamically create the conf.Settings class that's supposed to be supplied
by the user.  I have left the work that creates a file, compiles it and attempts to add it to the class path,
but I'm going to leave that part untested for now.
**/
class SettingsLoaderTests extends Spec with MustMatchers{
	
	describe("A SettingsLoader"){
		
		it("must throw an exception when conf.Settings cannot be found"){
			object TestSettingsLoader extends SettingsLoader
			intercept[ClassNotFoundException]{
				TestSettingsLoader.urlMappings
			}
		}
		
	}
	
	//compiles a test version of Settings.scala - this file will not exist in the scales.core code because it is provided by the client
	//so we create a fake on here and set the classpath
	def compile(): Unit = {
		//setup compiler
		val settings = new Settings()
		val srcDir = new File(System.getProperty("java.io.tmpdir") + "/src")
		//put the temp Settings.scala in the src dir
		putSettingsInTmpDir(srcDir)
		val outDir = new File(System.getProperty("java.io.tmpdir") + "/build")
	    settings.sourcepath.value = srcDir.getPath
    	settings.outdir.value = outDir.getPath
		println(new File(".").getAbsolutePath)
		settings.classpath.value = "core/target/classes"
    	object compiler extends Global(settings){}
		
		//compile
    	val run = new compiler.Run
    	run.compileFiles((srcDir.listFiles map io.AbstractFile.getFile).toList)
    	if (compiler.reporter.hasErrors) {
      		throw new RuntimeException("Compilation failed")
    	}

		//add compiled class to the classpath 
		val sysloader = ClassLoader.getSystemClassLoader().asInstanceOf[URLClassLoader];
		val sysclass = classOf[URLClassLoader]
		val parameters = scala.Array[Class[URL]](classOf[URL])
		val method = sysclass.getDeclaredMethod("addURL", classOf[URL]);
		method.setAccessible(true);
		method.invoke(sysloader, new File(System.getProperty("java.io.tmpdir") + "/build").toURL)
		()
	}
	
	//this creates the file to be compiled in a tmpdir.
	def putSettingsInTmpDir(tmpDir: File): Unit = {
		val str = """
		package conf

		import scales.Page
		import scales.conf.Config
		import scala.util.matching.Regex

		object Settings extends Config{
			type URLMapping = (Regex, Class[P] forSome {type P <: Page})
			def urlMappings: List[URLMapping] = ("/thisisatest.html".r, classOf[Page]) :: Nil
		}
		"""
		
		val stream = new FileOutputStream(new File(tmpDir.getPath + "/Settings.scala"));
		stream.write(str.getBytes());
		stream.flush();
		stream.close();
	}
	
}