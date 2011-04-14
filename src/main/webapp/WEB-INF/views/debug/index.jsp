<html>
    <head><title>Welcome to Java servlet web application</title></head>
	<body>
		<h1>Welcome to a simple Java servlet application!</h1>
		
		<p>Your application is ready, and it's currently been served from directory: 
		<pre><%= application.getRealPath("/") %></pre>
		</p>
		
		<h2>Utilities</h2>
		<ul>
		<li><a href="${pageContext.request.contextPath}/hello">Hello Servlet</a></li>
		<li><a href="${pageContext.request.contextPath}/requestinfo.jsp">Request Information</a></li>
		<li><a href="${pageContext.request.contextPath}/variables.jsp">JSP Implicit Variables</a></li>
		<li><a href="${pageContext.request.contextPath}/classloader.jsp">ClassLoader</a></li>
		<li><a href="${pageContext.request.contextPath}/threaddump.jsp">Thread Dumnp</a></li>
		</ul>
	</body>
</html>