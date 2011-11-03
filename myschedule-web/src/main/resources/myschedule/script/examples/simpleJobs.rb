# Groovy Examples

# Using myschedule.quartz.extra.SchedulerTemplate API
# ===================================================
require 'java'
java_import Java::MyscheduleQuartzExtraJob::LoggerJob
java_import Java::MyscheduleQuartzExtraJob::ScriptingJob
java_import Java::MyscheduleQuartzExtra::SchedulerTemplate
java_import Java::JavaUtil::Date
java_import Java::JavaLang::System

# Schedule hourly job
scheduler.schedule_simple_job("hourlyJob", -1, 60 * 60 * 1000, LoggerJob.java_class)

# Schedule minutely job
scheduler.schedule_simple_job("minutelyJob", -1, 60 * 1000, LoggerJob.java_class)

# Schedule secondly job
scheduler.schedule_simple_job("secondlyJob", -1, 1000, LoggerJob.java_class)

# Schedule secondly job that repeat total of 3 times.
scheduler.schedule_simple_job("secondlyJobRepeat3", 3, 1000, LoggerJob.java_class)

# Schedule onetime job that run immediately
scheduler.schedule_simple_job("onetimeJob", 1, 0, LoggerJob.java_class)

# Schedule hourly job with job data and start time of 20s delay.
scheduler.schedule_simple_job("hourlyJobWithStartTimeDelay", -1, 60 * 60 * 1000, ScriptingJob.java_class, 
		SchedulerTemplate.mk_map(
			'ScriptEngineName', 'Groovy', 
			'ScriptText', '''
				logger.info("I am a script job...")
				sleep(700L)
				logger.info("I am done.")
			'''
		), 
		Date.new(System.current_time_millis() + 20 * 1000))

# Schedule one job with multiple triggers
require 'java'
java_import Java::OrgQuartz::JobKey
java_import Java::MyscheduleQuartzExtraJob::LoggerJob
java_import Java::MyscheduleQuartzExtra::SchedulerTemplate

job = SchedulerTemplate.create_job_detail(JobKey.job_key("jobWithMutltipleTriggers"), LoggerJob.java_class, true, nil)
scheduler.add_job(job, false)
trigger1 = SchedulerTemplate.create_simple_trigger("trigger1", -1, 60 * 60 * 1000) # hourly trigger
trigger1.set_job_key(job.get_key())
scheduler.schedule_job(trigger1)
trigger2 = SchedulerTemplate.create_simple_trigger("trigger2", -1, 60 * 1000) # minutely trigger
trigger2.set_job_key(job.get_key())
scheduler.schedule_job(trigger2)

# Using Java org.scheduler.Scheduler API
# ===================================================
require 'java'
java_import Java::OrgQuartz::JobBuilder
java_import Java::OrgQuartz::TriggerBuilder
java_import Java::OrgQuartz::SimpleScheduleBuilder
java_import Java::MyscheduleQuartzExtraJob::LoggerJob

quartzScheduler = scheduler.getScheduler()
job = JobBuilder.
	newJob(LoggerJob.java_class).
	withIdentity("hourlyJob2").
	build()
trigger = TriggerBuilder.
	newTrigger().
	withSchedule(
		SimpleScheduleBuilder.repeatHourlyForever()).
	build()
quartzScheduler.scheduleJob(job, trigger)
