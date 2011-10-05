/*
// Delete all jobs first!!!!
count = 0
schedulerTemplate.jobDetails.each{ job -> 
  schedulerTemplate.deleteJob(job.name, job.group)
  webOut.println("job deleted: " + job.name)
  count += 1
}
webOut.println(count + " jobs deleted.")
*/

// Test out the misfire handler, you would need this config.
/*
# A Quartz 1.8.x Scheduler configuration for database job persistence.
# See http://www.quartz-scheduler.org/docs/configuration
#
# Note: setting up a quartz database schema is a manual process, and you may get 
# a copy of the database schema here:
# http://svn.terracotta.org/fisheye/browse/Quartz/tags/quartz-2.0.2/docs/dbTables

org.quartz.scheduler.skipUpdateCheck = false
org.quartz.scheduler.instanceName = DatabaseScheduler
org.quartz.scheduler.instanceId = NON_CLUSTERED
org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.dataSource = quartzDataSource
org.quartz.jobStore.tablePrefix = QRTZ_
org.quartz.jobStore.misfireThreshold = 5000
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount = 5

org.quartz.scheduler.batchTriggerAcquisitionMaxCount = 1
org.quartz.scheduler.batchTriggerAcquisitionFireAheadTimeWindow = 0

# Persistence Job Store - with database
org.quartz.dataSource.quartzDataSource.driver = com.mysql.jdbc.Driver
org.quartz.dataSource.quartzDataSource.URL = jdbc:mysql://localhost:3306/quartz2
org.quartz.dataSource.quartzDataSource.user = quartz2
org.quartz.dataSource.quartzDataSource.password = quartz2123
org.quartz.dataSource.quartzDataSource.maxConnections = 8

# MySchedule scheduler service parameters
myschedule.schedulerService.autoStart = true
myschedule.schedulerService.waitForJobsToComplete = true
*/

// Repeating jobs
7.times { i ->
	name = 'SleepyJob' + i
	job = schedulerTemplate.createJobDetail(name, GroovyScriptJob.class)
	job.getJobDataMap().put('groovyScriptText', 'logger.info("Pause for 7 secs."); sleep(7000)')
	trigger = schedulerTemplate.createSimpleTrigger(name, -1, 30000)
	scheduler.scheduleJob(job, trigger)
	webOut.println(name + ' has been scheduled.')
}

// 3 times jobs
7.times { i ->
	name = 'SleepyJob' + i
	job = schedulerTemplate.createJobDetail(name, GroovyScriptJob.class)
	job.getJobDataMap().put('groovyScriptText', 'logger.info("Pause for 7 secs."); sleep(7000)')
	trigger = schedulerTemplate.createSimpleTrigger(name, 2, 30000)
	scheduler.scheduleJob(job, trigger)
	webOut.println(name + ' has been scheduled.')
}