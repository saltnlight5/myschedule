//Create Quartz Calendar objects
importClass(Packages.org.quartz.impl.calendar.CronCalendar);
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);

var cal = CronCalendar('* * * * JAN ?');
scheduler.addCalendar('SkipJan', cal, true, false);
var cal = CronCalendar('* * * ? * SAT,SUN');
scheduler.addCalendar('SkipWeekEnd', cal, true, false);
var cal = CronCalendar('* * 12-13 * * ?');
scheduler.addCalendar('SkipLunch', cal, true, false);

// Create jobs that uses the calendars.
var job = scheduler.createJobDetail('CalJob', LoggerJob);
var trigger = scheduler.createCronTrigger('CalJob1', '0 0 * * * ?');
trigger.setCalendarName('SkipWeekEnd');
scheduler.scheduleJob(job, trigger);

var job = scheduler.createJobDetail('CalJob2', LoggerJob);
var trigger = scheduler.createCronTrigger('CalJob2', '0 0 * * * ?');
trigger.setCalendarName('SkipJan');
scheduler.scheduleJob(job, trigger);

var job = scheduler.createJobDetail('CalJob3', LoggerJob);
var trigger = scheduler.createCronTrigger('CalJob3', '0 0 * * * ?');
trigger.setCalendarName('SkipLunch');
scheduler.scheduleJob(job, trigger);
