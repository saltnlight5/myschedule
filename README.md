# Project

MySchedule is a web application for managing Quartz Scheduler.

For more information, see project home at http://code.google.com/p/myschedule


# Getting the source code

This project is using Mercurial source control and you may get the source code by running this:

	bash> hg clone https://code.google.com/p/myschedule
	bash> cd myschedule

There are few named branches in the repository to track different developments, and they are:

* default => The latest development of MySchedule 3.0.0_q21 for quartz 2.1.x
* myschedule-2.4.x => Use to maintain the web app for Quartz 2.1.x library using JQuery and table layout.
* myschedule-1.x => Use to maintain the web app for Quartz 1.8.x library using JQuery and table layout.

You may switch to any of the branch name or any released tag name to view the source. For example try any of the
following:

	bash> hg update myschedule-2.4.4  # A specific release source code
	bash> hg update myschedule-2.4.x  # The 2.4.x maintenance branch code
	bash> hg update default           # Back to the latest development code
	

# How to build this project (generate latest jar and war files etc)

Ensure you have JDK6+ and Maven3 installed, then run

	bash> mvn install


# How to run the application

You may deploy the `myschedule/myschedule-web/target/myschedule.war` into any Java Servlet container server.

Or if you want to run it directly from this project source using maven, then try

	bash> cd myschedule-web
	bash> mvn tomcat7:run

Then open a browser to http://localhost:8080/myschedule-web
