<%@ page import="javax.naming.*" %>

<pre>

<% 

Thread currentThread = Thread.currentThread();
ClassLoader contextCL = currentThread.getContextClassLoader();
out.println("This bean current Thread: " + currentThread);
out.println("This bean current Thread.ClassLoader: " + contextCL); 
out.println("This bean current Thread.ClassLoader.getResource(\"/\"): " + contextCL.getResource("/"));
out.println("This bean current Thread.ClassLoader.getResource(\"/\").getPath(): " + contextCL.getResource("/").getPath());
out.println("This bean current Thread.ClassLoader.getResource(\"/\").getFile(): " + contextCL.getResource("/").getFile()); 

%>
</pre>