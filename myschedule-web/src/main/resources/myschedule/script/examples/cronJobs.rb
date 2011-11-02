# JRuby Examples

# Using myschedule.quartz.extra.SchedulerTemplate API
# ===================================================

# For more on CRON expression format, see http:#www.quartz-scheduler.org/api/2.1.0/org/quartz/CronExpression.html
require 'java'
java_import Java::MyscheduleQuartzExtraJob::LoggerJob
java_import Java::MyscheduleQuartzExtra::SchedulerTemplate
java_import Java::JavaUtil::Date
java_import Java::JavaLang::System

# Schedule hourly job on every MON-FRI
scheduler.schedule_cron_job("hourlyCronJob", "0 0 * ? * MON-FRI", LoggerJob.java_class)

# Schedule minutely job on JUN and DEC only
scheduler.schedule_cron_job("minutelyCronJob", "0 * * * JUN,DEC ?", LoggerJob.java_class)

# Schedule secondly job
scheduler.schedule_cron_job("secondlyCronJob", "* * * * * ?", LoggerJob.java_class)

# Schedule hourly job with job data and start time of 20s delay.
scheduler.schedule_cron_job("hourlyCronJobWithStartTimeDelay", "0 0 * * * ?", LoggerJob.java_class, 
		SchedulerTemplate.mk_map('color', 'RED'), 
		Date.new(System.current_time_millis() + 20 * 1000))

# Schedule one job with multiple triggers
require 'java'
java_import Java::OrgQuartz::JobKey
java_import Java::MyscheduleQuartzExtraJob::LoggerJob
java_import Java::MyscheduleQuartzExtra::SchedulerTemplate

job = SchedulerTemplate.create_job_detail(JobKey.job_key("jobWithMutltipleTriggers2"), LoggerJob.java_class, true, nil)
scheduler.add_job(job, false)
trigger1 = scheduler.schedule_cron_job("cronTrigger1", "0 0 * * * ?") # hourly trigger
trigger1.set_job_key(job.get_key())
scheduler.schedule_job(trigger1)
trigger2 = scheduler.schedule_cron_job("cronTrigger2", "0 * * * * ?") # minutely trigger
trigger2.set_job_key(job.get_key())
scheduler.schedule_job(trigger2)

# Using Java org.scheduler.Scheduler API
# ===================================================
require 'java'
java_import Java::OrgQuartz::JobBuilder
java_import Java::OrgQuartz::TriggerBuilder
java_import Java::OrgQuartz::CronScheduleBuilder
java_import Java::MyscheduleQuartzExtraJob::LoggerJob

quartzScheduler = scheduler.getScheduler()
job = JobBuilder.
	newJob(LoggerJob.java_class).
	withIdentity("hourlyCronJob2").
	build()
trigger = TriggerBuilder.
	newTrigger().
	withSchedule(
		CronScheduleBuilder.cronSchedule("0 0 * * * ?")).
	build()
quartzScheduler.scheduleJob(job, trigger)
