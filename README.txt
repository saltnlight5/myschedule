= TODO =
 * Fix "Run It Now" to use a non-volatile trigger.
 * Add JMX enabled config sample.
 * Refactor the SchedulerService lifecycle's methods and match more to Scheduler interface instead.
 * Enhanced dashboard view with shutdown/initialize actions.
 * Enhanced scheduler settings view with pause/resume/standby/start submenu.
 
 * Disabled dashboard scheduler name link after shutdown.
 * Add groovy form action exception msg in the form display.
 
 * Add xml load action exception msg in the form display.
 * Add "Calendar" view to Job list.
 * Add calendar to calculation list of next trigger times.
 * Add calendars and listeners list page to Job page.
 * Add JobExecutionHistory plugin - record for when job started, completed, misfired, and by which trigger.

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


= samples =
== Add a job without trigger #2 ==
import org.quartz.*
job = new JobDetail('job', 'DEFAULT', myschedule.job.sample.SimpleJob.class)
quartzScheduler.addJob(job, true)

== Add a job without trigger =
import org.quartz.*
import org.quartz.jobs.*
job = new JobDetail('job1', 'DEFAULT', NoOpJob.class)
quartzScheduler.addJob(job, true)

== Simplest groovy job ==
// simple job that run every 3 secs.
{{{
schedulerService.createGroovyScriptCronJob('test', '0/3 * * * * ?', '''
  logger.info("Hi. This is a groovy script job.")
''')
}}}

// run job every 5 mins. the job will take 2 min to run.
schedulerService.createGroovyScriptCronJob('sleepy_job', '0 0/5 * * * ?', '''
  logger.info("Start sleeping...")
  Thread.sleep(2 * 60* 1000)
  logger.info("Job is done.")
''')
 
= Ideas =
 * Support quartz 2.0
 * CronTab
 * Cluster Monitoring

