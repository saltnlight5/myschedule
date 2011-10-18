= About myschedule =
The myschedule project is a web based Quartz scheduler dashboard application.
You may use to manage and monitor the scheduler through an easy to use web UI.

The project home page is at: http://code.google.com/p/myschedule

= Getting started =
To build this project source, you would need Maven 3 build tool, and run:
	mvn package

To run this project with latest code, you may try using the Maven tomcat plugin. Try to run:
	mvn tomcat:run

= Mercurial (Hg) Repository and Branches =
You may browse the code online here: http://code.google.com/p/myschedule/source/browse?repo=default

Current default branch is for myschedule-2.x work, and it uses Quartz 2.0 library.

There is a myschedule-1.x branch that uses Quartz 1.8.x library, and it's not compatible with 2.x.

To check out source, run:
	$ hg clone https://code.google.com/p/myschedule

= Support and Reporting Bugs =
Do you have a question on MySchedule project? Or you find a bug? Please help file
an Issue in http://code.google.com/p/myschedule/issues/list

= License and Credits =
The myschedule project is created in 2011 by Zemian Deng <saltnlight5@gmail.com>.

The project is an Open Source, and licensed with Apache License 2.0.

Feel free to contribute, provide feedback and ask questions through the project site.

= Production Deployment Notes =
The default myscheduler.war file can be drop into any Servlet 2.5 + Web Container and it should work.

The myscheduler.war has been tested on tomcat-6.0.32 and tomcat-7.0.8. You may start Tomcat in the
foreground by their script like this:
	$ cd $TOMCAT_HOME
	$ bin/catalina.bat run

= Performing Releases =
In Windows + Cygwin, you must use a Mercurial for Windows (hg.exe) in PATH in order to work with Maven release plugin!

$ mvn release:prepare
$ mvn release:perform

If you need to clean up file when things went wrong, try:
$ mvn release:clean

* Note that the default install repository for the release is  configured to local ${user.home}/.m2/repository directory.

= Want more? =
Check out some related, experimental projects in here:
(you will find notes on how to run myschedule.war in JBoss6 etc.) 
http://code.google.com/p/myschedule/source/browse/?repo=experiment
