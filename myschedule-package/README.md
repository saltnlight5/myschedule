# Project

The MySchedule is a Java based web application for managing Quartz Schedulers.

For more information, see project home at http://code.google.com/p/myschedule

Author: Zemian Deng <saltnlight5@gmail.com>


# Getting started

## Web application usage (option 1: power up a self-contained servlet server!)
	
	bash> cd myschedule-3.*
	bash> bin/myschedule-ui.sh --httpPort=8081

Now open a browser and visit http://localhost:8081

## Web application usage (option 2: use your own servelt server)

Simply copy the myschedule-3.*/war/myschedule.war file into your Servlet container such as Tomcat.

## Command line usage (option 3: quickly test your quartz config)

	bash> cd myschedule-3.*
	bash> bin/myschedule.sh bin/quartz.properties
