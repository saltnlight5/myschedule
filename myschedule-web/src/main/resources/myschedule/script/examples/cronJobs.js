// JavaScript Examples

// Using myschedule.quartz.extra.SchedulerTemplate API
// ===================================================

// For more on CRON expression format, see http://www.quartz-scheduler.org/api/2.1.0/org/quartz/CronExpression.html
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.java.lang.System);
// Schedule hourly job on every MON-FRI
scheduler.scheduleCronJob("hourlyCronJob", "0 0 * ? * MON-FRI", Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule minutely job on JUN and DEC only
scheduler.scheduleCronJob("minutelyCronJob", "0 * * * JUN,DEC ?", Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule secondly job
scheduler.scheduleCronJob("secondlyCronJob", "* * * * * ?", Packages.myschedule.quartz.extra.job.LoggerJob);

// Schedule hourly job with job data and start time of 20s delay.
scheduler.scheduleCronJob("hourlyCronJobWithStartTimeDelay", "0 0 * * * ?", Packages.myschedule.quartz.extra.job.LoggerJob, 
		scheduler.mkMap('color', 'RED'), 
		Packages.java.util.Date(System.currentTimeMillis() + 20 * 1000));

// Schedule one job with multiple triggers
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

// Using Java org.scheduler.Scheduler API
// ===================================================
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
