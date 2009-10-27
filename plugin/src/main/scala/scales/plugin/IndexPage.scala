package scales.plugin

/**
Assets for the Index and Ajax pages created when <i>scales init</i> is called
**/
object IndexPage{
	
	/**
	The code for Index.scala - contains two clases, Index and Ajax
	**/
	def page: String = {
		"""
package pages

import _root_.scales.{Page, UsesLayout}
import _root_.layouts.Main
import _root_.javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import _root_.scala.xml.NodeSeq

class Index extends Page with UsesLayout[Main]{
	def layout = classOf[Main]
}

class Ajax extends Page{
	override def apply(request: HttpServletRequest, response: HttpServletResponse): NodeSeq = {
		<li>The server says it's {new java.util.Date().toString}</li>
	}
}
		
"""
	}

	/**
	The html for scales-app/pages/Index.html
	**/
	def resource: String = {
"""
<html>
	<head>
		<style type="text/css">
			h1, h2{
				text-align:center
			}

			input#ajax_button{
				margin:auto;
			}
			ul#ajax_list{
				padding:0;
				margin:10px 0 0 0;
			}
		</style>
		<script type="text/javascript">
			$(document).ready(function(){
				$("#ajax_button").click(function(){
					$.post("/ajax.html", null, function(data){
						$("ul#ajax_list").append(data);
					});
				});
			});
		</script>
		<title>Welcome to Scales!</title>
	</head>
	<body>
		<h1 id="welcome">Welcome to Scales</h1>
		<div id="ajax_container">
			<h2>Try out some AJAX</h2>
			<input type="button" name="ajax_button" id="ajax_button" value="AJAX"/>
			<ul id="ajax_list"></ul>
		</div>
	</body>
</html>
"""
	}
}