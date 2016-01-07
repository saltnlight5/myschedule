# Stable Releases #

## 3.2.1.0 (2013-08-24) for Quartz 2.1.7 ##

  * [Issue#120](https://code.google.com/p/myschedule/issues/detail?id=#120) - Upgrade Quartz library to 2.2.x

```
- Upgraded to Quartz 2.2.1
- Upgraded to SLF4J 1.7.5

- Quartz 2.2.1 API has some changes that break backward compatibility and this release addressed these
  issues:

  -- SchedulerPlugin interface now has different #initialize() signature
  -- SchedulerListener interface has added new method #schedulerStarting()
  -- Scheduler interface has couple new mehtods addJobs and schedulerJobs with Set instead of List.
```

## 3.2.0.0 (2013-06-08) for Quartz 2.1.7 ##

  * [Issue#104](https://code.google.com/p/myschedule/issues/detail?id=#104) - New release format to reflect new development work
    * We now use (3.2.0.0) as version label, where second number is the Quartz's major version.
  * [Issue#105](https://code.google.com/p/myschedule/issues/detail?id=#105) - Prepare myschedule-3.0.0 for new development branch of work
    * Re-org repository with the follwing named branches (they should be maintained as stable branches)
      * default - latest changes
      * myschedule-3.x - Vaddin UI + Quartz 2 stable branch
      * myschedule-2.x - JQuery UI + Quartz 2 stable branch
      * myschedule-1.x - JQuery UI + Quartz 1 stable branch
  * [Issue#109](https://code.google.com/p/myschedule/issues/detail?id=#109) - Add new `MySchedule` manager class for web layer management
    * Sub Task: Clean up old MVC request processing classes
    * Sub Task: Updated util: `Props` and `ClasspathURLStreamHandler`
    * Sub Task: Added `MySchedule`, `SchedulerSettings`, `MyScheduleSettings`
    * Sub Task: Added `SchedulerSettingsStore` and `TemplateStore`
  * [Issue#9](https://code.google.com/p/myschedule/issues/detail?id=#9) - Use Vaadin UI instead of jQuery/Table
    * Sub Task: Design UI top toolbar with navigational buttons
    * Sub Task: Design UI new scheduler config screen
    * Sub Task: Design UI dashboard/scheduler table screen
    * Sub Task: Design UI job/trigger table screen
    * Sub Task: Design UI script console screen
  * [Issue#110](https://code.google.com/p/myschedule/issues/detail?id=#110) -	Improve and refactor a `ScriptingUtils` that used in various places
  * [Issue#111](https://code.google.com/p/myschedule/issues/detail?id=#111) - Upgrade Quartz to 2.1.7
  * [Issue#112](https://code.google.com/p/myschedule/issues/detail?id=#112) - Use single project packaging distribution zip
    * Added bin/myschedule-ui.sh to startup a embedded web server!
    * Added war/myschedule.war distribution format.
    * Added lib/myschedule-quartz-extra.jar distribution format.
  * [Issue#108](https://code.google.com/p/myschedule/issues/detail?id=#108) - Create editable templates on UI
  * [Issue#117](https://code.google.com/p/myschedule/issues/detail?id=#117) - Added interrupt button to running jobs.
  * [Issue#113](https://code.google.com/p/myschedule/issues/detail?id=#113) - Migrate 2.x UI features to 3.0

## 3.1.0.0 (2013-06-08) - for Quartz 1.8.6 ##

  * Same features as above in `3.2.0.0` but back-ported to support Quartz 1.8.6 API.


# Older Releases #

Before myschedule-2.x, the release version format are organized as follow:

  * myschedule-2.x (war) is for quartz-2.x
  * myschedule-quartz-extra-2.x (jar) for quartz-2.x
  * myschedule-1.x (war) is for quartz-1.8.x
  * myschedule-quartz-extra-1.x (jar) for quartz-1.8.x

## myschedule-2.4.4.war and myschedule-quartz-extra-2.4.4.jar 08/21/2012 ##

Upgraded to latest Quartz release and some minor bug fixes.

  * http://code.google.com/p/myschedule/issues/detail?id=91
  * http://code.google.com/p/myschedule/issues/detail?id=92
  * http://code.google.com/p/myschedule/issues/detail?id=93
  * http://code.google.com/p/myschedule/issues/detail?id=94
  * http://code.google.com/p/myschedule/issues/detail?id=95

## myschedule-2.4.3.war and myschedule-quartz-extra-2.4.3.jar 06/02/2012 ##

> Some bug fixes and enhancements to the webapp.

  * http://code.google.com/p/myschedule/issues/detail?id=64
  * http://code.google.com/p/myschedule/issues/detail?id=68
  * http://code.google.com/p/myschedule/issues/detail?id=75
  * http://code.google.com/p/myschedule/issues/detail?id=78
  * http://code.google.com/p/myschedule/issues/detail?id=79
  * http://code.google.com/p/myschedule/issues/detail?id=80
  * http://code.google.com/p/myschedule/issues/detail?id=81
  * http://code.google.com/p/myschedule/issues/detail?id=82
  * http://code.google.com/p/myschedule/issues/detail?id=83
  * http://code.google.com/p/myschedule/issues/detail?id=84
  * http://code.google.com/p/myschedule/issues/detail?id=85
  * http://code.google.com/p/myschedule/issues/detail?id=86
  * http://code.google.com/p/myschedule/issues/detail?id=87
  * http://code.google.com/p/myschedule/issues/detail?id=88
  * http://code.google.com/p/myschedule/issues/detail?id=89
  * http://code.google.com/p/myschedule/issues/detail?id=90

## myschedule-2.4.2.war and myschedule-quartz-extra-2.4.2.jar 03/20/2012 ##

Minor bug fix and improvement.

  * http://code.google.com/p/myschedule/issues/detail?id=72
  * http://code.google.com/p/myschedule/issues/detail?id=73
  * http://code.google.com/p/myschedule/issues/detail?id=74
  * http://code.google.com/p/myschedule/issues/detail?id=76
  * http://code.google.com/p/myschedule/issues/detail?id=77

## myschedule-2.4.1.war 01/28/2012 ##

Minor bug fix and improvement.

  * http://code.google.com/p/myschedule/issues/detail?id=70
  * http://code.google.com/p/myschedule/issues/detail?id=71

## myschedule-2.4.0.war and myschedule-quartz-extra-2.4.0.jar | 12/28/2011 ##
  * Added JdbcSchedulerHistoryPlugin that will record job history in DB.
  * Added Cron Tool expression validation on web page.
  * Added AppConfig configurable by a Properties file.
  * Upgraded to quartz-2.1.1 and optional groovy-1.8.4.
  * Deployed a online demo onto Heroku platform.
  * Fixed trigger display page.

  * http://code.google.com/p/myschedule/issues/detail?id=54
  * http://code.google.com/p/myschedule/issues/detail?id=55
  * http://code.google.com/p/myschedule/issues/detail?id=56
  * http://code.google.com/p/myschedule/issues/detail?id=57
  * http://code.google.com/p/myschedule/issues/detail?id=58
  * http://code.google.com/p/myschedule/issues/detail?id=59
  * http://code.google.com/p/myschedule/issues/detail?id=60
  * http://code.google.com/p/myschedule/issues/detail?id=61
  * http://code.google.com/p/myschedule/issues/detail?id=62
  * http://code.google.com/p/myschedule/issues/detail?id=63
  * http://code.google.com/p/myschedule/issues/detail?id=65
  * http://code.google.com/p/myschedule/issues/detail?id=66
  * http://code.google.com/p/myschedule/issues/detail?id=67
  * http://code.google.com/p/myschedule/issues/detail?id=69

## myschedule-1.6.0.war and myschedule-quartz-extra-1.6.0.jar | 11/04/2011 ##
  * Backport of all 2.3.0 features for Quartz-1.8.x.

---


## myschedule-2.3.2.war and myschedule-quartz-extra-2.3.2.jar | 11/18/2011 ##
  * Fixed few more bugs.
  * [Resolved Issues](http://code.google.com/p/myschedule/issues/list?can=1&q=label%3AFixedRelease-2.3.2)

## myschedule-2.3.1.war and myschedule-quartz-extra-2.3.1.jar | 11/17/2011 ##
  * Bug fixes and clean up of JSP views.
  * Added more tests coverage.
  * [Resolved Issues](http://code.google.com/p/myschedule/issues/list?can=1&q=Release%3D2.3.1+&colspec=ID+Type+Status+Priority+Milestone+Owner+Summary&cells=tiles)

## myschedule-2.3.0.war and myschedule-quartz-extra-2.3.0.jar | 11/04/2011 ##

Many new features and bug fixes for managing Quartz-2.x

http://code.google.com/p/myschedule/issues/list?can=1&q=label%3ARelease-2.3.0

## myschedule-2.2.0.war and myschedule-quartz-extra-2.2.0.jar | 10/18/2011 ##
  * Works with Quartz-2.x
  * Extracted few reusable quartz components into its own library.
  * A cleaner SchedulerTemplate class
  * A reusable ScriptingJob that works with javax.scripting.ScriptEngine.
  * Provided a new ScriptingSchedulerPlugin that let you run script upon scheduler init, start and shutdown.
  * Added a OsCommandJob that let you execute external command more safely.
  * Added SchedulerMain command line tool.
  * Added full integration tests suite for all classes!
  * Generated online JavadocApi.
  * [Fixed Issues](http://code.google.com/p/myschedule/issues/list?can=1&q=label%3ARelease-2.2.0&colspec=ID+Type+Status+Priority+Milestone+Owner+Summary&cells=tiles)

## myschedule-2.1.2 | 09/24/2011 ##
  * http://code.google.com/p/myschedule/issues/detail?id=8
  * http://code.google.com/p/myschedule/issues/detail?id=16
  * http://code.google.com/p/myschedule/issues/detail?id=17

## myschedule-2.1.1 | 09/24/2011 ##
  * http://code.google.com/p/myschedule/issues/detail?id=6

## myschedule-2.1.0 | 09/23/2011 ##
  * Upgraded to quartz-2.1.0
  * Upgraded to groovy-1.8.2 (this version fixed JBoss deployment issue!)
  * Reworked backed services
    * Reworked AbastractService and ServiceContainer services.
    * New SchedulerConfig entity with configId, replacing direct use of scheduler name for UI requests.
    * New FileSchedulerConfigDao, SchedulerServiceRepo, SessionSchedulerServiceFinder implementations.
    * Added new methods to SchedulerTemplate for easy programming.
    * Updated views with latest backend changes.
  * Removed Service's Pause/Resume feature to avoid confusion. Just use Quartz Start/InStandyMode/Shutdown instead.
  * Added Pause/Resume All Triggers action to Scheduler Setting page.
  * Added TriggerState status display to Trigger Detail page.
  * Added new config samples for Create New Scheduler.
  * Resolved the following Issues:
    * http://code.google.com/p/myschedule/issues/detail?id=1
    * http://code.google.com/p/myschedule/issues/detail?id=3
    * http://code.google.com/p/myschedule/issues/detail?id=4

## myschedule-2.0.0 | 7/24/2011 ##
  * Upgraded to quartz-2.0.2
  * Refactored service classes to fix quartz 2.0 API breakage.
  * Updated view to fix quartz 2.0 API breakage.
  * Replaced commons-dbcp to c3p0 database conn pooling library.
  * Create "myschedule-1.x" hg branch for older quartz-1.8 work.
  * Use "default" hg for myschedule-2.x work.
  * Groovy Script now has auto import of following packages: `import org.quartz.*, import org.quartz.job.*, import myschedule.job.*, import myschedule.job.sample.*`
  * Added many utilities methods in SchedulerTemplate for easy scripting.


---

## myschedule-1.5.2 | 09/24/2011 ##
  * http://code.google.com/p/myschedule/issues/detail?id=16
  * http://code.google.com/p/myschedule/issues/detail?id=17

## myschedule-1.5.1 | 09/24/2011 ##
  * http://code.google.com/p/myschedule/issues/detail?id=6
  * http://code.google.com/p/myschedule/issues/detail?id=8
  * http://code.google.com/p/myschedule/issues/detail?id=15

## myschedule-1.5.0 | 9/17/2011 ##
  * Backport all the latest work from 2.x into 1.x branch.

## myschedule-1.4.3 | 7/23/2011 ##
  * Fixed NPE bug when starting up without config file directory created.

## myschedule-1.4.2 | 7/23/2011 ##
  * Fixed calendar datetime bg highlight displayed even when calendar is not set.
  * Added SchedulerTemplate class and refactored services to use it.
  * Added "schedulerTemplate" groovy variable to Scripting.

## myschedule-1.4.1 | 7/10/2011 ##
  * Fixed "Run It Now" to use a non-volatile trigger.
  * Added JMX enabled config sample.
  * Refactored the SchedulerService lifecycle's methods and match more to Scheduler interface instead.
  * Enhanced dashboard view with shutdown/initialize actions.
  * Enhanced scheduler settings view with pause/resume/standby/start submenu.
  * Disabled dashboard scheduler name link after shutdown.
  * Fixed bug in SchedulerController#modify-action when schedulerName has changed.
  * Fixed bug in  job/list fail on first startup.
  * Added groovy form action exception msg in the form display.
  * Added xml load action exception msg in the form display.
  * Added listeners page under Settings menu.
  * Added "Calendar" view to Job list.
  * Added calendar to calculation list of next trigger times.


## myschedule-1.4.0 | 7/4/2011 ##
  * Add modify action for config props in scheduler menu.
  * Add warning on non-started scheduler on job list page.
  * Add delete confirmation dialog

## myschedule-1.3.3 | 7/2/2011 ##
  * Bug Fix: Currently Executing Jobs page error (invalid fields on jsp)
  * Added "Run It Now" action on job list.
  * Added createGroovyScriptCronJob method to SchedulerService.

## myschedule-1.3.2 | 7/1/2011 ##
  * Bug fix: delete scheduler page error.
  * Bug fix: webapp startup error when scheduler failed to load (RMI client without server.)
  * Enhanced scheduler settings controller with start/pause/resume/shutdown.
  * Enhanced dashboard and job list view.
  * Added currently-executing-job list page.

## myschedule-1.3.1 | 7/1/2011 ##
  * Enhanced dashboard scheduler creation form with quick AJAX pull config samples.
  * Scheduler config properties are now persistent and survive webapp restart.
  * Added smart landing home page to redirect based on schedulers settings.
  * Removed default InMemory scheduler creation. User need to create their own scheduler upon first startup now.
  * Optimized sample configs settings for easier setup.
  * Enhanced usage of autoStart and waitForJobsToComplete settings.
  * Refactored many back-end classes to better support SchedulerService management.

## myschedule-1.3.0 | 6/28/2011 ##
  * Support Multiple Schedulers!
  * Removed @javax.annotation.Resource and use setter method on controllers.
  * Turn controller to have HttpSession param available.
  * New menu nagivation with dashboard link.
  * Improved table data display.

## myschedule-1.2.0 | 6/22/2011 ##
  * Enhanced UI look with jQueryUI theme and `jQuery.DataTables`.
  * Add sorting columns to job list
  * Re-add job name back into job list.
  * Switch maven jetty to tomcat plugin

## myschedule-1.1.3 | 6/17/2011 ##
  * Removed Oracle JDBC driver as default package.
  * Add sys prop to prevent auto startup and detect remote scheduler.
  * Improve Job list (triggers names only, then drill down to job details.)
  * Improve Job list (show more standard trigger info in columns.)
  * Improve Job list (separate job without trigger listing.)

## myschedule-1.1.2 | 6/10/2011 ##
  * Improved Web UI using CSS and jQuery.
  * Improved menu navigation and messaging display.
  * Refactored web controllers to better provide the web UI requests.
  * Changed the scripting to be more generic instead just to load jobs.
  * Added action for delete job and triggers.
  * Added action for unschedule job/trigger.
  * Added job detail and trigger detail UI web page.
  * Added example and help page in the UI.

## myschedule-1.0.0 | 06/5/2011 ##
  * Setup Maven project for war packaging build.
  * Provide dashboard web UI to manage quartz-1.8.x scheduler.
  * Provide scheduler summary and status.
  * Provide scheduler control to start/pause scheduler.
  * Provide Job listing and their triggers.
  * Provide each job's next fire times list.
  * Load job-scheduling-data xml file through web UI.
  * Use Groovy scripting to load jobs through web UI.