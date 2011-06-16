<%@ page import="org.quartz.simpl.*" %>
<pre>
<%
String resname = "org/quartz/xml/job_scheduling_data_1_8.xsd";
out.println("class: " + getClass());
out.println("classLoader: " + getClass().getClassLoader());
out.println("classLoader res: " + getClass().getClassLoader().getResource(resname));

CascadingClassLoadHelper clhelper = new CascadingClassLoadHelper();
clhelper.initialize();
out.println("Quartz clhelper res: " + clhelper.getResource(resname));

CascadingClassLoadHelper clhelper2 = new CascadingClassLoadHelper();
out.println("Quartz CascadingClassLoadHelper#getClassLoader(): " + clhelper2.getClassLoader());
%>
</pre>