// Groovy Examples

// Using myschedule.quartz.extra.SchedulerTemplate API
// ===================================================

// For more on CRON expression format, see http://www.quartz-scheduler.org/api/2.1.0/org/quartz/CronExpression.html
import myschedule.quartz.extra.job.LoggerJob
// Schedule hourly job on every MON-FRI
scheduler.scheduleCronJob("hourlyCronJob", "0 0 * ? * MON-FRI", LoggerJob.class)

// Schedule minutely job on JUN and DEC only
scheduler.scheduleCronJob("minutelyCronJob", "0 * * * JUN,DEC ?", LoggerJob.class)

// Schedule secondly job
scheduler.scheduleCronJob("secondlyCronJob", "* * * * * ?", LoggerJob.class)

// Schedule hourly job with job data and start time of 20s delay.
scheduler.scheduleCronJob("hourlyCronJobWithStartTimeDelay", "0 0 * * * ?", LoggerJob.class, 
		scheduler.mkMap('color', 'RED'), 
		new Date(System.currentTimeMillis() + 20 * 1000))

// Schedule one job with multiple triggers
import org.quartz.*
import myschedule.quartz.extra.job.LoggerJob
job = scheduler.createJobDetail(JobKey.jobKey("jobWithMutltipleTriggers2"), LoggerJob.class, true, null)
scheduler.addJob(job, false)
trigger1 = scheduler.createCronTrigger("cronTrigger1", "0 0 * * * ?") // hourly trigger
trigger1.setJobKey(job.getKey())
scheduler.scheduleJob(trigger1)
trigger2 = scheduler.createCronTrigger("cronTrigger2", "0 * * * * ?") // minutely trigger
trigger2.setJobKey(job.getKey())
scheduler.scheduleJob(trigger2)

// Using Java org.scheduler.Scheduler API
// ===================================================
import org.quartz.*
import myschedule.quartz.extra.job.LoggerJob
quartzScheduler = scheduler.getScheduler()
job = JobBuilder
	.newJob(LoggerJob.class)
	.withIdentity("hourlyCronJob2")
	.build()
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
	.build()
quartzScheduler.scheduleJob(job, trigger)
