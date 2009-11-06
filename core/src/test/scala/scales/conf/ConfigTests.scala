package scales.conf

import org.scalatest.{Spec, BeforeAndAfter}
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._

import scala.xml._
import java.net._
import java.io._

class ConfigTests extends Spec with MustMatchers with BeforeAndAfter {
	
	override def afterEach(){
		System.setProperty("environment", "")
	}

	override def beforeEach(){
		System.setProperty("environment", "")
	}
	
	
	describe("An Environment"){
		
		it("must read the development environment by default"){
			object Foo extends Config{
				def urlMappings = null
			}
			
			Foo.environment must equal(Development)
		}
		
		it("must read the test environment from System.getProperty"){
			object Foo extends Config{
				def urlMappings = null
			}
			System.setProperty("environment", "test")
			Foo.environment must equal(Test)
			
		}
		
		it("must read the production environment from System.getProperty"){
			object Foo extends Config{
				def urlMappings = null
			}
			System.setProperty("environment", "production")
			Foo.environment must equal(Production)
		}
	}
}