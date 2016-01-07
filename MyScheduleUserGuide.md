# Introduction #

The [Quartz Scheduler](http://quartz-scheduler.org) is a Java library that supports flexible scheduling such as fixed repeating interval or CRON jobs. Developers may write custom job task in Java and use Quartz triggers to schedule the job. Quartz also has optional database level data storage, and it allow fine control on job executions with thread pool and clustering capabilities.

Although Quartz is a powerful library, but it's target users are maily developers. You can not get a job scheduled without writing some Java code. There is also no visual presentation on the scheduled jobs other than a application log output. The `MySchedule` project here provides not only some extra quartz components for easy programming, but we also provide an end-user friendly way to access and manage the Quartz scheduler. You can get it up and running with little or no programming necessary. It will host and run the full scheduler inside the servlet application.

<table>
<tr valign='top'>

<td>
Features list:<br>
<ul><li>Extra Quartz components such as reusable Jobs and Plugins.<br>
</li><li>A web based, user friendly UI to Quartz scheduler system.<br>
</li><li>Runs on any Java Servlet 2.5 Web Container such as Tomcat server.<br>
</li><li>Create, modify, and delete multiple scheduler configurations on web UI.<br>
</li><li>Dashboard view of all schedulers.<br>
</li><li>Run, pause, resume, and stop scheduler through the web UI without restarting server.<br>
</li><li>Drill down view on each scheduler's jobs, triggers, and scheduler runtime information.<br>
</li><li>View scheduler's calendars and listeners components.<br>
</li><li>Trigger preview on next fire times with calendar exclusion highlight.<br>
</li><li>Load jobs using Quartz's xml scheduling job data on web UI.<br>
</li><li>Use dynamic scripting language to create and manage jobs and scheduler at runtime.<br>
</li><li>Open source and free to use with Apache License 2.0<br>
</td></li></ul>

<td>
<br />
<a href='http://code.google.com/p/myschedule/wiki/ReleaseNotes'>ReleaseNotes</a>

<a href='http://code.google.com/p/myschedule/wiki/MyScheduleUserGuide'>MySchedule User Guide</a>

<a href='http://code.google.com/p/myschedule/wiki/QuartzExtraUserGuide'>QuartzExtra User Guide</a>

<a href='http://code.google.com/p/myschedule/wiki/DevelopmentGuide'>MySchedule Project Development Guide</a>

<a href='http://code.google.com/p/myschedule/wiki/Faq'>FAQ</a>

<a href='http://code.google.com/p/myschedule/wiki/JavadocApi'>Javadoc API</a>

<a href='http://code.google.com/p/myschedule/wiki/MyScheduleScreenshots'>Webapp Screenshots </a>

<a href='http://code.google.com/p/myschedule/wiki/OnlineDemo'>Webapp Online Demo</a>

<a href='WeWantToGive.md'>Donations</a>
</td>

</tr>
</table>

# Requirements #
  * Java 6+
  * Servlet 2.5 + Web Container such as Tomcat server.
  * Write access to your `$HOME/.myschedule3` directory. (Or `%USERPROFILE%\.myschedule3` in Windows.)
  * Optional database such as Mysql. (Only needed if you want to configure scheduler persistent storage.)

# Installation #

Download and unzip the binary package.

You may simply copy the "war/myschedule.war" into any of external servlet container you already have.


# Web User Interface #
Your home page will land on the dashboard list page. You may click on the "Create Scheduler" to create one (The configuration will persists even if you restart your webapp.) If you are not familiar with Quartz configuration, there are few examples config links avaiable, and once you click on it, it should auto populate the form for you.

After you created a scheduler config, you may click on the scheduler name link to view the job list for that scheduler.

Once you are in job list view, your top menu bar will change and the current scheduler name is displayed. There are few main top menu links: Jobs, Settings, Scripting, or back to Dashboard links. They are briefly described here:

Jobs - Control Jobs in scheduler. You can view all the jobs, delete, pause, resume or create new jobs. You can create new job by loading the Quartz's job-scheduling-data xml file, or create a small script to schedule any custome jobs.

Settings - Give scheduler summary and details status. You can also control the scheduler here by putting it to standby mode or re-start it. You may also edit configuration here.

Scripting - For the power user, you may work with the scheduler using a scripting language (default is `JavaScript`). A good use case is to load large number of test jobs into the scheduler. Or if you just need to manipulate the scheduler using full power of a programming language. You may see many examples in ScriptingScheduler page.

Dashboard - The scheduler Dashboard view let you manage multiple schedulers on the same JVM that you have setup in your webapp. You may create new scheduler or delete them. When deleting scheduler, you are only deleting the config properties and remove it from the webapp. The actual job persistent data (if you used database scheduler that's is) will still remain!

# Setting up logging #
The MySchedule already package with log4j, so if you use Tomcat, simply add a file `log4j.properties` in $TOMCAT\_HOME/lib directory like this:
```
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.conversionPattern=%d %-5p %t %c:%L| %m%n
log4j.rootLogger=INFO, CONSOLE
log4j.logger.myschedule=INFO
```

# Creating User Jobs #
It will not do much good if the scheduler will not let you run a job that does what you want. See CreatingUserJobs for more examples on how to create your own custom jobs.

# Changing `MySchedule` Configuration #
The `MySchedule` webapp supports mutiple schedulers management. You may create, modify or delete any of scheduler's configuration without having to restart the web server. The configuration is a Java Properties formatted text that will be persisted in your user home directory under `$HOME/myschedule2/configs` or `$HOME/myschedule1/configs`, depending on the version used. These are done so your schdeuler will be able reload with same settings when you restart your web server. Note that if you've created an In-Memory scheduler, all job data will be lost when you restart it!

Quartz is flexible in allowing you to set different configurations for different schedulers need. You may create each scheduler instance with customized Java properties configuration through our web UI. The only Quartz restriction is that you must name your scheduler `org.quartz.scheduler.instanceName` unique within a single JVM (server).

Note that if you use `MySchedule-1.x`/Quartz 1.x database job persistent configuration, you must create new database (or schema) with unique table prefix for each unique scheduler name. But in `MySchedule-2.x`/Quartz 2.x, it will allow single database per multiple schedulers configuration.

Full documentation on Quartz configuration (`quartz.properties`) can be found at http://www.quartz-scheduler.org/documentation

NOTE: If you use quartz in clustered mode, you must start each scheduler node in it's own JVM (eg: tomcat server instance, not just different webapp context), and each cluster node MUST have the same scheduler `org.quartz.scheduler.instanceName` set, but with different `org.quartz.scheduler.instanceId` for each scheduler node. Also default Quartz cluster will only work if you use database job storage!

You may want to check out DatabaseConfiguration page for more info.

## Auto Init and Start of Scheduler in `MySchedule` ##
The scheduler you create using Java properties may have setting to tell `MyScheduler` whether to auto start the scheduler upon the web application is started or not. You may change this by using these keys:

```
# MySchedule scheduler service parameters (These are not used by quartz itself, but for the webapp only.)
myschedule.schedulerService.autoInit = true
myschedule.schedulerService.autoStart = true
myschedule.schedulerService.waitForJobsToComplete = true
```

The "waitForJobsToComplete" will wait for your current running jobs to complete first to shutdown gracefully.

You may add these along in the `quartz.properties` of your scheduler configuration!

Also, the `MySchedule` webapp have some implementation specific configuration that you may change well (such as location of the file storage directory etc.). To change these, use Java System Properties like this:
```
-Dmyschedule.config=classpath:myschedule/cutom-myschedule-config.properties
```

You can change properties such as these:
```
#myschedule.configStore.class = myschedule.service.MemConfigStore
myschedule.configStore.class = myschedule.service.FileConfigStore
myschedule.configStore.FileConfigStore.directory = ${user.home}/myschedule2/configs
myschedule.handlers.JobHandler.defaultFireTimesCount = 20
myschedule.web.pauseAfterShutdown = 1000
```

See the complete list here: http://code.google.com/p/myschedule/source/browse/myschedule-web/src/main/resources/myschedule/myschedule-config.default.properties

## Deploying multiple instances of `MySchedule` webapp ##
Remember each `MySchedule` will use a config directory for storage, so when running multiple instance of webapp, you want to ensure they are unique. And because Quartz doesn't allow you to have duplicated scheduler instance name per JVM, you must use different config storage directory per each webapp that you deploy on the same JVM server.