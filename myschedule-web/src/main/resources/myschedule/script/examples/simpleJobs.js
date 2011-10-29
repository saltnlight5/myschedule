// Using myschedule.quartz.extra.SchedulerTemplate API
// ===================================================
// Schedule hourly job
scheduler.scheduleSimpleJob("hourlyJob", -1, 60 * 60 * 1000, Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule minutely job
scheduler.scheduleSimpleJob("minutelyJob", -1, 60 * 1000, Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule secondly job
scheduler.scheduleSimpleJob("secondlyJob", -1, 1000, Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule secondly job that repeat total of 3 times.
scheduler.scheduleSimpleJob("secondlyJobRepeat3", 3, 1000, Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule onetime job that run immediately
scheduler.scheduleSimpleJob("onetimeJob", 1, 0, Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule hourly job with job data and start time of 20s delay.
scheduler.scheduleSimpleJob("hourlyJobWithStartTimeDelay", -1, 60 * 60 * 1000, Packages.myschedule.quartz.extra.job.ScriptingJob, 
		scheduler.mkMap(
			'ScriptEngineName', 'Groovy', 
			'ScriptText', 
				'logger.info("I take 3 secs to run...");\n' +
				'sleep(3000L);\n' +
				'logger.info("I am done.");\n'
		), 
		Packages.java.util.Date(Packages.java.lang.System.currentTimeMillis() + 20 * 1000));

// Schedule one job with multiple triggers
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail(JobKey.jobKey("jobWithMutltipleTriggers"), Packages.myschedule.quartz.extra.job.LoggerJob, true, null);
scheduler.addJob(job, false);
trigger1 = scheduler.createSimpleTrigger("trigger1", -1, 60 * 60 * 1000); // hourly trigger
trigger1.setJobKey(job.getKey());
scheduler.scheduleJob(trigger1);
trigger2 = scheduler.createSimpleTrigger("trigger2", -1, 60 * 1000); // minutely trigger
trigger2.setJobKey(job.getKey());
scheduler.scheduleJob(trigger2);

// Using Java org.scheduler.Scheduler API
// ===================================================
importPackage(Packages.org.quartz);
quartzScheduler = scheduler.getScheduler();
job = JobBuilder
	.newJob(Packages.myschedule.quartz.extra.job.LoggerJob)
	.withIdentity("hourlyJob2")
	.build();
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		SimpleScheduleBuilder.repeatHourlyForever())
	.build();
quartzScheduler.scheduleJob(job, trigger);
