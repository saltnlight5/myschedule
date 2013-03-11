# Project

MySchedule is a web application for managing Quartz Scheduler.

For more information, see project home at http://code.google.com/p/myschedule


# Getting the source code

	bash> hg clone https://code.google.com/p/myschedule
	bash> cd myschedule

The default branch is the latest development of 3.x release work and it may not be stable yet!

If you want an exact release source code, then simply switch to the tag name like this:

	bash> hg update myschedule-2.4.4

Or if you want the 2.x maintence branch source code, then switch to the branch name like this:

	bash> hg update myschedule-2.x

Or to get back to latest development branch, then switch like this:

	bash> hg update default
	

# How to build this project (generate latest jar and war files etc)

Ensure you have JDK6+ and Maven3 installed, then run

	bash> mvn install


# How to run the application

You may deploy the `myschedule/myschedule-web/target/myschedule.war` into any Java Servlet container server.

Or if you want to run it directly from this project source, then try

	bash> mvn install
	bash> cd myschedule-web
	bash> mvn tomcat7:run

Then visit http://localhost:8080/myschedule


# Versioning and Maintence Branches

This project is using Mercurial source control and we are using the following workflow branches

* default => The latest development branch (might not be stable)
* myschedule-3.x => Stable branch to maintain the 3.x release series (Vaadin UI with Quartz 2).
* myschedule-2.x => Stable branch to maintain the 2.x release series (JQuery UI with Quartz 2).
* myschedule-1.x => Stable branch to maintain the 1.x release series (JQuery UI with Quartz 1).
