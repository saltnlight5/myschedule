# Project

MySchedule is a web application for managing Quartz Scheduler.

For more information, see project home at http://code.google.com/p/myschedule

# How to build this project (generate jar and war files)

Ensure you have JDK6+ and Maven3 installed, then run

bash> mvn install

# How to run the application

You may deploy the `myschedule.war` into any Java Servlet container server.

Or if you want to run it directly from this project source, run

bash> mvn install
bash> cd myschedule-web
bash> mvn tomcat7:run

Then visit http://localhost:8080/myschedule