/*
// Delete all jobs first!!!!
count = 0
scheduler.allJobDetails.each{ job -> 
  scheduler.deleteJob(job.key)
  webOut.println("job deleted: " + job.key)
  count += 1
}
webOut.println(count + " jobs deleted.")
*/

// Create long running job
job = scheduler.createJobDetail('LongRunningJob', myschedule.quartz.extra.job.ScriptingJob.class)
map = job.getJobDataMap()
map.put('ScriptEngineName', 'Groovy')
map.put('ScriptText', 'logger.info("will sleep a min."); sleep(60000)')
trigger = scheduler.createCronTrigger('LongRunningJob', '0 5/0 * * * ?')
scheduler.scheduleJob(job, trigger)

// Create Calendars
import org.quartz.impl.calendar.*

cal = new CronCalendar('* * * * JAN ?')
scheduler.addCalendar('SkipJan', cal, true, false)

cal = new CronCalendar('* * * ? * SAT,SUN')
scheduler.addCalendar('SkipWeekEnd', cal, true, false)

cal = new CronCalendar('* * 12-13 * * ?')
scheduler.addCalendar('SkipLunch', cal, true, false)

// Jobs that uses the calendars.

job = scheduler.createJobDetail('CalJob', myschedule.quartz.extra.job.LoggerJob.class)
trigger = scheduler.createCronTrigger('CalJob1', '0 0 * * * ?')
trigger.setCalendarName('SkipWeekEnd')
scheduler.scheduleJob(job, trigger)

job = scheduler.createJobDetail('CalJob2', myschedule.quartz.extra.job.LoggerJob.class)
trigger = scheduler.createCronTrigger('CalJob2', '0 0 * * * ?')
trigger.setCalendarName('SkipJan')
scheduler.scheduleJob(job, trigger)

job = scheduler.createJobDetail('CalJob3', myschedule.quartz.extra.job.LoggerJob.class)
trigger = scheduler.createCronTrigger('CalJob3', '0 0 * * * ?')
trigger.setCalendarName('SkipLunch')
scheduler.scheduleJob(job, trigger)

// Create Job Detail without triggers
5.times { i->
  job = scheduler.createJobDetail('JobWithoutT' + i, myschedule.quartz.extra.job.LoggerJob.class)
  job.setDurability(true)
  scheduler.addJob(job, false)
}

// Create one JobDetail with multiple Triggers
job = scheduler.createJobDetail('MyJob', org.quartz.jobs.NoOpJob.class)
job.setDurability(true)
scheduler.addJob(job, false)

5.times { i->
  trigger = scheduler.createCronTrigger('MyJobTrigger' + i, '0 0 0 * * ?')
  trigger.setJobKey(job.key)
  scheduler.scheduleJob(trigger)
}