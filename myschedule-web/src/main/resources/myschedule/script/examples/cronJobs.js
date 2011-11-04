// JavaScript Examples

// Using myschedule.quartz.extra.SchedulerTemplate API
// ===================================================

// For more on CRON expression format, see http://www.quartz-scheduler.org/api/2.1.0/org/quartz/CronExpression.html
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.java.lang.System);
importClass(Packages.java.util.HashMap);

// Schedule hourly job on every MON-FRI
scheduler.scheduleCronJob("hourlyCronJob", "0 0 * ? * MON-FRI", LoggerJob);

// Schedule minutely job on JUN and DEC only
scheduler.scheduleCronJob("minutelyCronJob", "0 * * * JUN,DEC ?", LoggerJob);

// Schedule secondly job
scheduler.scheduleCronJob("secondlyCronJob", "* * * * * ?", LoggerJob);

// Schedule hourly job with job data and start time of 20s delay.
dataMap = HashMap();
dataMap.put('color', 'RED');
scheduler.scheduleCronJob("hourlyCronJobWithStartTimeDelay", "0 0 * * * ?", LoggerJob,
		dataMap, 
		Packages.java.util.Date(System.currentTimeMillis() + 20 * 1000));

// Schedule one job with multiple triggers
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail("jobWithMutltipleTriggers2", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, false);
trigger1 = scheduler.createCronTrigger("cronTrigger1", "0 0 * * * ?"); // hourly trigger
trigger1.setJobName(job.getName());
scheduler.scheduleJob(trigger1);
trigger2 = scheduler.createCronTrigger("cronTrigger2", "0 * * * * ?"); // minutely trigger
trigger2.setJobName(job.getName());
scheduler.scheduleJob(trigger2);

// Using Java org.scheduler.Scheduler API
// ===================================================
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
quartzScheduler = scheduler.getScheduler();
job = JobDetail("hourlyCronJob2", "DEFAULT", LoggerJob);
trigger = CronTrigger("hourlyCronJob2", "DEFAULT", "0 0 * * * ?");
quartzScheduler.scheduleJob(job, trigger);
