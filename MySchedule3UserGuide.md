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

<a href='http://code.google.com/p/myschedule/wiki/MySchedule3UserGuide'>MySchedule User Guide</a>

<a href='http://code.google.com/p/myschedule/wiki/QuartzExtraUserGuide'>QuartzExtra User Guide</a>

<a href='http://code.google.com/p/myschedule/wiki/JavadocApi'>Javadoc API</a>

<a href='http://code.google.com/p/myschedule/wiki/Faq'>FAQ</a>

<a href='http://code.google.com/p/myschedule/wiki/DevelopmentGuide'>MySchedule Project Development Guide</a>

<a href='WeWantToGive.md'>Donations</a>

<a href='http://demo-myschedule.rhcloud.com'>MySchedule Online Demo</a>
</td>

</tr>
</table>

# Requirements #
  * Java 6+
  * Servlet 2.5 + Web Container such as Tomcat server.
  * Write access to your `$HOME/.myschedule3` directory.
  * Optional database such as MySQL. (Only needed if you want to configure scheduler persistent storage.)

# Installation #

Download and unzip the binary package.

NOTE: The `MySchedule-3.2.x` is for managing latest `Quartz-2.x` API while `MySchedule-3.1.x` is for managing older `Quartz-1.x` API. These API are not backward compatible.

# Running the application #

You may try running standalone embedded web server
```
bash> bin/myschedule-ui.sh
```

Or simply copy the "war/myschedule.war" into any of external servlet container you already have.

Or you may also run it as standalone java application without an UI.
```
bash> bin/myschedule.sh /path/to/quartz.properties
```

# Web User Interface #

## Schedulers Dashboard (Home) Screen ##

When the web application first started, the home page is the Schedulers Dashboard Screen. This screen will list all of Quartz schedulers with it's name, configuration file id, scheduler status and number of jobs count. You may control the scheduler operations by first highlight the row record, then the action buttons will be enabled.

  * **View Details** - After you selected a scheduler, you may drill down into its details. You may do the same by double clicking on the scheduler row as well. You only can see details after the Scheduler is initialized.
  * **Init** - Initialize a Quartz Scheduler object from the config file. Quartz force creation of the object and initialize resources at the same time. This means if you configured database option, it MUST be available before you click Init button.
  * **Start** - Starting the Scheduler will begin check any jobs that needs to be run.
  * **Standby** - Put the Scheduler in standby (not to run any jobs) mode.
  * **Shutdown** - Bring down the Scheduler system and destroy the instance. After you shutdown, only config file remain, and you must click "Init" in order to instantiate a new Scheduler instance to run again.
  * **Delete** - Shutdown a Scheduler and delete its configuration file.
  * **Edit** - Edit the Scheduler configuration settings. Scheduler will be shutdown and then init again upon save.
  * **New** - Create a new Scheduler configuration settings file and scheduler instance entry in Dashboard screen. Once you create a configuration settings, it will be persist even if you bring down the `MySchedule` web application. The configuration settings are simply Quartz configuration.

## Scheduler Details Screen ##

Using **View Detail** button from the dashboard you will come into the Scheduler Details Screen. This screen will provide you views to all the jobs and its related information per this scheduler specific. You may also manage it through UI controls provided here.

On top are the "Breadcrum" navigation that let you go back to Dashboard, or refresh this scheduler details screen.

Below the breadcrum are the scheduler "Tab" views and controls. Each tab gives you a different table view of information and a toolbar to control it. Some tab will bring up a popup Window to edit information.

  * **Jobs with Triggers** tab - This shows a table of all jobs in scheduler (`JobDetail` that has `Trigger` associated. The table will shows Trigger Name/Group, JobDetail Name/Group, Class Name of Trigger/JobDetail, Next Job Run Time, Previous Job Run Time, and Trigger status. The toolbar in this tab will have these controls, and they will be enabled when you select a row.
    * **Refresh** - Refresh the table view in case there are more jobs added by external source.
    * **View Details** - View more detailed information on the Trigger and JobDetail information, such as their data map, misfire instruction etc. There is also a set of future firetimes to preview when the job will run.
    * **Delete** - Delete the Trigger. If the JobDetail is non-durable and there is no other Trigger associated with it, then it will also be deleted. Otherwise, you should see othe entry remained in the table, or they will be in the "Jobs without Triggers" tab view.
    * **Run It Now** - Add a new Trigger that immediately execute that JobDetail definition.
    * **Pause** - Pausing a single trigger will prevent the job to run when its time is due. You will see this indicated by the "Status" column in the table.
  * **Jobs without Triggers** tab - This shows a table of all JobDetail definition that are durable and without Trigger associated with. These will not be run but just the definition remain. The table view information are similar to the previous, minus the Trigger information.
  * **Jobs without Triggers** tab - This shows a table of current execution jobs (Triggers that has just fired, but the Job is not finished yet.) Note that most of Job will be executed fast, and you will likely not see them in here. This view is useful if you have some long running Job, then you may inspect them here.
  * **Calendars/Exclusion** tab - This shows a table of Quartz Calendar objects. These are mostly used to associate to a Trigger to exclude dates/times to NOT run the scheduled job.
  * **Scheduler Status** tab - This shows a table of Quartz Scheduler runtime meta-data information. There is also a table to show all the registered Listeners in the Quartz system. And lastly it shows a table list of all the plugins found from the configuration settings.
  * **Xml Job Loader** tab - This shows a popup Windows to allow you enter XML content to load JobDetail definition and Trigger. This provide as job entry to the scheduler. It's functionality is actually provided by Quartz plugin, and it's been refitted to load from UI instead of configuration plugin style. In this popup Windows, you may even save the XML content into a template of your own for re-use in future.
  * **Job Histories** tab - This shows a a table view of all the job runs histories record. This is not part of Quartz standard and only provided by MySchedule Quartz Extra libary. You must configure the plugin plus setup extra database table to record the job run events to be display here.
  * **Script Console** tab - This shows a popup Windows to let you manage the Scheduler instance using any Java enabled scripting language. The JDK comes with JavaScript built-in. Many developers will find Groovy as alternative and easier to use. You would need to add each additional scripting language support by adding their "jar" files into the web server classpath. You may also save script content as template for future re-use. There is no limit as what you can do with script and you will have the "scheduler" implicit variable exposed to you.


# Setting up logging #
The MySchedule already package with log4j, and log at INFO level. You may override it per your servlet container classpath location. For example if you use Tomcat, simply add a file `log4j.properties` in $TOMCAT\_HOME/lib directory like this:
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
The `MySchedule` webapp supports mutiple schedulers management. You may create, modify or delete any of scheduler's configuration without having to restart the web server. The configuration is a Java Properties formatted text that will be persisted in your user home directory under `$HOME/.myschedule3`. These are done so your scheduler will be able reload with same settings when you restart your web server. Note that if you've created an In-Memory scheduler, all job data will be lost when you restart it!

Quartz is flexible in allowing you to set different configurations for different schedulers need. You may create each scheduler instance with customized Java properties configuration through our web UI. The only Quartz restriction is that you must name your scheduler `org.quartz.scheduler.instanceName` unique within a single JVM (server).

Note that if you use `MySchedule-1.x`/Quartz 1.x database job persistent configuration, you must create new database (or schema) with unique table prefix for each unique scheduler name. But in `MySchedule-2.x`/Quartz 2.x, it will allow single database per multiple schedulers configuration.

Full documentation on Quartz configuration (`quartz.properties`) can be found at http://www.quartz-scheduler.org/documentation

NOTE: If you use quartz in clustered mode, you must start each scheduler node in it's own JVM (eg: tomcat server instance, not just different webapp context), and each cluster node MUST have the same scheduler `org.quartz.scheduler.instanceName` set, but with different `org.quartz.scheduler.instanceId` for each scheduler node. Also default Quartz cluster will only work if you use database job storage!

You may want to check out DatabaseConfiguration page for more info.

## Auto Init and Start of Scheduler in `MySchedule` ##
The scheduler you create using Java properties may have setting to tell `MyScheduler` whether to auto start the scheduler upon the web application is started or not. You may add the following directly inside the `quartz.properties` Quartz config file. However it will only be used by MySchedule, and not Quartz.

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
-Dmyschedule.settings=/path/to/myschedule-settings.properties
```

You can change properties such as these. If you ommit them, they will use the following as default values.
```
# MySchedule data store dir.
myschedule.web.dataStoreDir = ${user.home}/.myschedule3

# Directory where scheduler settings (quartz config properties + MySchedule settings) files are stored
myschedule.web.schedulerSettingsDir = ${myschedule.web.dataStoreDir}/scheduler-configs

# Directory where scheduler settings templates are stored
myschedule.web.schedulerTemplatesDir = ${myschedule.web.dataStoreDir}/templates/scheduler-configs

# Directory where script templates are stored
myschedule.web.scriptTemplatesDir = ${myschedule.web.dataStoreDir}/templates/scripts

# Directory where xml job loader templates are stored
myschedule.web.xmlJobLoaderTemplatesDir = ${myschedule.web.dataStoreDir}/templates/xmljobloader

# Automatically create a scheduler if MySchedule startup empty. Set it to empty to skip this.
# This will also be use to set as default "New Scheduler" UI editor form content.
myschedule.web.defaultSchedulerSettings = classpath:///myschedule/web/scheduler.properties

# Preferred script engine to use (default selection) in the ScriptConsole window
myschedule.web.defaultScriptEngineName = JavaScript

# Number of firetimes to preview when view job trigger detail information
myschedule.web.numOfFiretimesPreview = 20

# Name of key to the Scheduler Context to retrieve JdbcSchedulerHistoryPlugin instance.
myschedule.web.JdbcSchedulerHistoryPluginContextKey = JdbcSchedulerHistoryPlugin.Instance

# Amount of time to pause web server after scheduler shutdown. Unit=milliseconds. O means OFF.
myschedule.web.pauseTimeAfterShutdown = 1000
```

## Deploying multiple instances of `MySchedule` webapp ##
Remember each `MySchedule` will use a config directory for storage, so when running multiple instance of webapp, you want to ensure they are unique. And because Quartz doesn't allow you to have duplicated scheduler instance name per JVM, you must use different config storage directory per each webapp that you deploy on the same JVM server.