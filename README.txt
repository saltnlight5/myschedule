This is a Java Servlet web application that host a Quartz Scheduler instance.
You may use this as a Quartz Dashboard.

== Deployment Notes ==
The default myscheduler.war file can be drop into any Servlet 2.5 + Web Container and it should work.

The myscheduler.war has been tested on jetty-8.0.0.M2 (via maven plugin), tomcat-6.0.32 and tomcat-7.0.8.

To run myscheduler.war on Tomcat with different quartz properties file, try:
(Under Windows Cygwin)
$ export JAVA_OPTS="-Dmyschedule.quartz.config=file:///C:\projects\myschedule-experiment\quartz-1.8-experiment\config\quartz.properties.database"
$ bin/catalina.bat run