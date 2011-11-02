# JRuby Examples

# Create Quartz Calendar objects
require 'java'
java_import Java::OrgQuartzImplCalendar::CronCalendar
java_import Java::MyscheduleQuartzExtraJob::LoggerJob
java_import Java::MyscheduleQuartzExtra::SchedulerTemplate

cal = CronCalendar.new('* * * * JAN ?')
scheduler.add_calendar('SkipJan', cal, true, false)
cal = CronCalendar.new('* * * ? * SAT,SUN')
scheduler.add_calendar('SkipWeekEnd', cal, true, false)
cal = CronCalendar.new('* * 12-13 * * ?')
scheduler.add_calendar('SkipLunch', cal, true, false)

# Create jobs that uses the calendars.
job = SchedulerTemplate.create_job_detail('CalJob', LoggerJob.java_class)
trigger = SchedulerTemplate.create_cron_trigger('CalJob1', '0 0 * * * ?')
trigger.set_calendar_name('SkipWeekEnd')
scheduler.schedule_job(job, trigger)

job = SchedulerTemplate.create_job_detail('CalJob2', LoggerJob.java_class)
trigger = SchedulerTemplate.create_cron_trigger('CalJob2', '0 0 * * * ?')
trigger.set_calendar_name('SkipJan')
scheduler.schedule_job(job, trigger)

job = SchedulerTemplate.create_job_detail('CalJob3', LoggerJob.java_class)
trigger = SchedulerTemplate.create_cron_trigger('CalJob3', '0 0 * * * ?')
trigger.set_calendar_name('SkipLunch')
scheduler.schedule_job(job, trigger)