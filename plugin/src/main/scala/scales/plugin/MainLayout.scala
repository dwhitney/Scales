package scales.plugin

/**
Assets for the Main layout created when <i>scales init</i> is called
**/
object MainLayout{
	
	/**
	The code for scales-app/layouts/Main.scala
	**/
	def page: String = {
"""
package layouts

import _root_.scales.Layout

class Main extends Layout
"""
	}
	
	/**
	The html for scales-app/Layouts/Main.html
	**/
	def resource: String = {
"""		
<html xmlns:s="http://www.scales-framework.org" xmlns="http://www.w3.org/1999/xhtml/" xml:lang="en" lang="en">
	<head>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
		<style type="text/css">
			html{
				background:#2F92DC;
				font-family:helvetica, arial;
			}
			div#container{
				background:white;
				margin: auto;
				width: 500px;
				border:solid black 10px;
				padding:10px;
				-moz-border-radius:15px;
				text-align:center;
			}
		</style>
		<s:layout node="head" childrenOnly="true"/>
	</head>
	<body>
		<div id="container">
			<s:layout node="body" childrenOnly="true"/>
		</div>
	</body>
</html>
"""		
}
	
}