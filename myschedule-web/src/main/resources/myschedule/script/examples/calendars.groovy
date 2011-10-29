// Groovy Examples

// Create Quartz Calendar objects
import org.quartz.impl.calendar.*
import myschedule.quartz.extra.job.LoggerJob

cal = new CronCalendar('* * * * JAN ?')
scheduler.addCalendar('SkipJan', cal, true, false)
cal = new CronCalendar('* * * ? * SAT,SUN')
scheduler.addCalendar('SkipWeekEnd', cal, true, false)
cal = new CronCalendar('* * 12-13 * * ?')
scheduler.addCalendar('SkipLunch', cal, true, false)

// Create jobs that uses the calendars.
job = scheduler.createJobDetail('CalJob', LoggerJob.class)
trigger = scheduler.createCronTrigger('CalJob1', '0 0 * * * ?')
trigger.setCalendarName('SkipWeekEnd')
scheduler.scheduleJob(job, trigger)

job = scheduler.createJobDetail('CalJob2', LoggerJob.class)
trigger = scheduler.createCronTrigger('CalJob2', '0 0 * * * ?')
trigger.setCalendarName('SkipJan')
scheduler.scheduleJob(job, trigger)

job = scheduler.createJobDetail('CalJob3', LoggerJob.class)
trigger = scheduler.createCronTrigger('CalJob3', '0 0 * * * ?')
trigger.setCalendarName('SkipLunch')
scheduler.scheduleJob(job, trigger)