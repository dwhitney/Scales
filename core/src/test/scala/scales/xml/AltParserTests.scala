package scales.xml

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.mockito.Mockito._

import scala.xml._

class AltParserTests extends Spec with MustMatchers{
	
	describe("An AltParser"){
		
		it("must not escape quotes"){
			val xml = 
<html>
	<head>
		<script type="text/javascript">
			alert("Foo!");
		</script>
		<title>Hello, World</title>
	</head>
</html>

	val string = """<html>
	<head>
		<script type="text/javascript">
			alert("Foo!");
		</script>
		<title>Hello, World</title>
	</head>
</html>"""
			
			AltParser.toXML(xml, false, false, false) must equal(string)
			()
		}
		
		it("must parse additional xml"){
			val xml = 
<html>
	<head>
		<script type="text/javascript">
			alert("Foo!");
		</script>
		<title>Hello, World</title>
	</head>
	<body>
		<s:layout node="body" childrenOnly="true"/>
	</body>
</html>

			val string = """<html>
	<head>
		<script type="text/javascript">
			alert("Foo!");
		</script>
		<title>Hello, World</title>
	</head>
	<body>
		<s:layout node="body" childrenOnly="true"></s:layout>
	</body>
</html>"""
			AltParser.toXML(xml, false, false, false) must equal(string)
		}
		
	}
	
}