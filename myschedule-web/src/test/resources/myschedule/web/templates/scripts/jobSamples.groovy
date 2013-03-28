package myschedule.web.templates.scripts
// Simple Jobs
// ===========

// Using myschedule.quartz.extra.SchedulerTemplate API
// ===================================================
import myschedule.quartz.extra.job.LoggerJob
import myschedule.quartz.extra.job.ScriptingJob

// Schedule hourly job
scheduler.scheduleSimpleJob("hourlyJob", -1, 60 * 60 * 1000, LoggerJob.class)

// Schedule minutely job
scheduler.scheduleSimpleJob("minutelyJob", -1, 60 * 1000, LoggerJob.class)

// Schedule secondly job
scheduler.scheduleSimpleJob("secondlyJob", -1, 1000, LoggerJob.class)

// Schedule secondly job that repeat total of 3 times.
scheduler.scheduleSimpleJob("secondlyJobRepeat3", 3, 1000, LoggerJob.class)

// Schedule onetime job that run immediately
scheduler.scheduleSimpleJob("onetimeJob", 1, 0, LoggerJob.class)

// Schedule hourly job with job data and start time of 20s delay.
scheduler.scheduleSimpleJob("hourlyJobWithStartTimeDelay", -1, 60 * 60 * 1000, ScriptingJob.class,
		scheduler.mkMap(
			'ScriptEngineName', 'Groovy',
			'ScriptText', '''
				logger.info("I am a script job...")
				sleep(700L)
				logger.info("I am done.")
			'''
		),
		new Date(System.currentTimeMillis() + 20 * 1000))

// Schedule one job with multiple triggers
import org.quartz.*
import myschedule.quartz.extra.job.LoggerJob
job = scheduler.createJobDetail(JobKey.jobKey("jobWithMutltipleTriggers"), LoggerJob.class, true, null)
scheduler.addJob(job, false)
trigger1 = scheduler.createSimpleTrigger("trigger1", -1, 60 * 60 * 1000) // hourly trigger
trigger1.setJobKey(job.getKey())
scheduler.scheduleJob(trigger1)
trigger2 = scheduler.createSimpleTrigger("trigger2", -1, 60 * 1000) // minutely trigger
trigger2.setJobKey(job.getKey())
scheduler.scheduleJob(trigger2)

// Using Java org.scheduler.Scheduler API
// ===================================================
import org.quartz.*
import myschedule.quartz.extra.job.LoggerJob
quartzScheduler = scheduler.getScheduler()
job = JobBuilder
	.newJob(LoggerJob.class)
	.withIdentity("hourlyJob2")
	.build()
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		SimpleScheduleBuilder.repeatHourlyForever())
	.build()
quartzScheduler.scheduleJob(job, trigger)
