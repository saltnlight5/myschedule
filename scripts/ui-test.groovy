// Create long running job
job = schedulerTemplate.createJobDetail('LongRunningJob', GroovyScriptJob.class)
job.getJobDataMap().put('groovyScriptText', 'logger.info("will sleep a min."); sleep(60000)')
trigger = schedulerTemplate.createCronTrigger('LongRunningJob', '0 5/0 * * * ?')
scheduler.scheduleJob(job, trigger)

// Create Calendars
import org.quartz.impl.calendar.*

cal = new CronCalendar('* * * * JAN ?')
quartzScheduler.addCalendar('SkipJan', cal, true, false)

cal = new CronCalendar('* * * ? * SAT,SUN')
quartzScheduler.addCalendar('SkipWeekEnd', cal, true, false)

cal = new CronCalendar('* * 12-13 * * ?')
quartzScheduler.addCalendar('SkipLunch', cal, true, false)

// Jobs that uses the calendars.

job = schedulerTemplate.createJobDetail('CalJob', SimpleJob.class)
trigger = schedulerTemplate.createCronTrigger('CalJob1', '0 0 * * * ?')
trigger.setCalendarName('SkipWeekEnd')
scheduler.scheduleJob(job, trigger)

job = schedulerTemplate.createJobDetail('CalJob2', SimpleJob.class)
trigger = schedulerTemplate.createCronTrigger('CalJob2', '0 0 * * * ?')
trigger.setCalendarName('SkipJan')
scheduler.scheduleJob(job, trigger)

job = schedulerTemplate.createJobDetail('CalJob3', SimpleJob.class)
trigger = schedulerTemplate.createCronTrigger('CalJob3', '0 0 * * * ?')
trigger.setCalendarName('SkipLunch')
scheduler.scheduleJob(job, trigger)

// Create Job Detail without triggers
5.times { i->
  job = schedulerTemplate.createJobDetail('JobWithoutT' + i, myschedule.job.sample.SimpleJob.class)
  job.setDurability(true)
  schedulerTemplate.addJob(job, false)
}

// Create one JobDetail with multiple Triggers
job = schedulerTemplate.createJobDetail('MyJob', org.quartz.jobs.NoOpJob.class)
job.setDurability(true)
schedulerTemplate.addJob(job, false)

5.times { i->
  trigger = schedulerTemplate.createCronTrigger('MyJobTrigger' + i, '0 0 0 * * ?')
  trigger.setJobName('MyJob')
  schedulerTemplate.scheduleJob(trigger)
}