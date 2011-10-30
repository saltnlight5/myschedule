// JavaScript Examples

// Using myschedule.quartz.extra.SchedulerTemplate API
// ===================================================
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
