= About myschedule =
The myschedule project is a web based Quartz scheduler dashboard application.
You may use to manage and monitor the scheduler throhgh an easy to use web UI.

The project home page is at: http://code.google.com/p/myschedule

= Credits =
The myschedule project is created by Zemian Deng <saltnlight5@gmail.com>.

= Deployment Notes =
The default myscheduler.war file can be drop into any Servlet 2.5 + Web Container and it should work.

The myscheduler.war has been tested on tomcat-6.0.32 and tomcat-7.0.8.

To run myscheduler.war on Tomcat with different quartz properties file, try:
(Under Windows Cygwin)
$ export JAVA_OPTS="-Dmyschedule.quartz.config=file:///C:/projects/myschedule/src/main/resources/myschedule/spring/scheduler/quartz.properties.database"
$ bin/catalina.bat run

= TODO =
 
