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

# Versioning and Maintence Branches

This project is stored in Mercurial with the following branches

default => The latest development: Redesign web app using Vaadin
           The version label format is 3.0.0_q2 for quartz 2.x, and 3.0.0_q1 for quartz 1.x.
myschedule-2.4 => Use to maintain the web app for Quartz 2.1.x library using JQuery and table layout.
myschedule-1.x => Use to maintain the web app for Quartz 1.8.x library using JQuery and table layout.