// Simple Jobs
// ===========

importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.myschedule.quartz.extra.job.ScriptingJob);
importClass(Packages.java.lang.System);
importClass(Packages.java.util.HashMap);

//Schedule hourly job
scheduler.scheduleSimpleJob("hourlyJob", -1, 60 * 60 * 1000, LoggerJob);

// Schedule minutely job
scheduler.scheduleSimpleJob("minutelyJob", -1, 60 * 1000, LoggerJob);

// Schedule secondly job
scheduler.scheduleSimpleJob("secondlyJob", -1, 1000, LoggerJob);

// Schedule secondly job that repeat total of 3 times.
scheduler.scheduleSimpleJob("secondlyJobRepeat3", 3, 1000, LoggerJob);

// Schedule onetime job that run immediately
scheduler.scheduleSimpleJob("onetimeJob", 1, 0, LoggerJob);

// Schedule hourly job with job data and start time of 20s delay.
dataMap = HashMap();
dataMap.put('ScriptEngineName', 'JavaScript');
dataMap.put('ScriptText', 
		'logger.info("I am a script job...");\n' +
		'Packages.java.lang.Thread.sleep(700);\n' +
		'logger.info("I am done.");\n');
scheduler.scheduleSimpleJob("hourlyJobWithStartTimeDelay", -1, 60 * 60 * 1000, ScriptingJob, 
		dataMap, 
		Packages.java.util.Date(System.currentTimeMillis() + 20 * 1000));

// Schedule one job with multiple triggers
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail(JobKey.jobKey("jobWithMutltipleTriggers"), LoggerJob, true, null);
scheduler.addJob(job, false);
trigger1 = scheduler.createSimpleTrigger("trigger1", -1, 60 * 60 * 1000); // hourly trigger
trigger1.setJobKey(job.getKey());
scheduler.scheduleJob(trigger1);
trigger2 = scheduler.createSimpleTrigger("trigger2", -1, 60 * 1000); // minutely trigger
trigger2.setJobKey(job.getKey());
scheduler.scheduleJob(trigger2);

// Using Java org.scheduler.Scheduler API
// ===================================================
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
quartzScheduler = scheduler.getScheduler();
job = JobBuilder
	.newJob(LoggerJob)
	.withIdentity("hourlyJob2")
	.build();
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		SimpleScheduleBuilder.repeatHourlyForever())
	.build();
quartzScheduler.scheduleJob(job, trigger);


// Cron Jobs
// =========
//JavaScript Examples

//Using myschedule.quartz.extra.SchedulerTemplate API
//===================================================

//For more on CRON expression format, see http://www.quartz-scheduler.org/api/2.1.0/org/quartz/CronExpression.html
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.java.lang.System);
importClass(Packages.java.util.HashMap);

//Schedule hourly job on every MON-FRI
scheduler.scheduleCronJob("hourlyCronJob", "0 0 * ? * MON-FRI", LoggerJob);

//Schedule minutely job on JUN and DEC only
scheduler.scheduleCronJob("minutelyCronJob", "0 * * * JUN,DEC ?", LoggerJob);

//Schedule secondly job
scheduler.scheduleCronJob("secondlyCronJob", "* * * * * ?", LoggerJob);

//Schedule hourly job with job data and start time of 20s delay.
dataMap = HashMap();
dataMap.put('color', 'RED');
scheduler.scheduleCronJob("hourlyCronJobWithStartTimeDelay", "0 0 * * * ?", LoggerJob,
		dataMap, 
		Packages.java.util.Date(System.currentTimeMillis() + 20 * 1000));

//Schedule one job with multiple triggers
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail(JobKey.jobKey("jobWithMutltipleTriggers2"), LoggerJob, true, null);
scheduler.addJob(job, false);
trigger1 = scheduler.createCronTrigger("cronTrigger1", "0 0 * * * ?"); // hourly trigger
trigger1.setJobKey(job.getKey());
scheduler.scheduleJob(trigger1);
trigger2 = scheduler.createCronTrigger("cronTrigger2", "0 * * * * ?"); // minutely trigger
trigger2.setJobKey(job.getKey());
scheduler.scheduleJob(trigger2);

//Using Java org.scheduler.Scheduler API
//===================================================
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
quartzScheduler = scheduler.getScheduler();
job = JobBuilder
	.newJob(LoggerJob)
	.withIdentity("hourlyCronJob2")
	.build();
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
	.build();
quartzScheduler.scheduleJob(job, trigger);

// Job without triggers
// ====================

// Create some durable jobs without triggers.
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail(JobKey.jobKey("durableJob1"), LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail(JobKey.jobKey("durableJob2"), LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail(JobKey.jobKey("durableJob3"), LoggerJob, true, null);
scheduler.addJob(job, false);

// Long Running Jobs
// =================
//Create a job that takes 15 secs to run.
importClass(Packages.myschedule.quartz.extra.job.ScriptingJob);
importClass(Packages.java.lang.System);
importClass(Packages.java.util.HashMap);
dataMap = HashMap();
dataMap.put('ScriptEngineName', 'JavaScript');
dataMap.put('ScriptText', 
		'logger.info("I take 15 secs to run...");\n' +
		'Packages.java.lang.Thread.sleep(15000);\n' +
		'logger.info("I am done.");\n');
scheduler.scheduleSimpleJob("15secJob", -1, 60 * 60 * 1000, ScriptingJob, 
		dataMap, 
		Packages.java.util.Date(System.currentTimeMillis() + 20 * 1000));
