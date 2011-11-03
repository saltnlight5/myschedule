// JavaScript Examples

// Using CalendarIntervalTrigger
//   schedule a job that runs every 3 months
// =========================================
importPackage(Packages.org.quartz);
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);

job = JobBuilder
	.newJob(LoggerJob)
	.withIdentity("calendarIntervalJob")
	.build();
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		CalendarIntervalScheduleBuilder
		.calendarIntervalSchedule()
		.withIntervalInMonths(3))
	.build();
scheduler.scheduleJob(job, trigger);

// Using DailyTimeIntervalTrigger
//   schedule a job that runs every 72 mins MON-FRI from 8:00AM to 5:00PM
// ======================================================================
importPackage(Packages.org.quartz);
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);

job = JobBuilder
	.newJob(LoggerJob)
	.withIdentity("dailyTimeIntervalJob")
	.build();
trigger = TriggerBuilder
	.newTrigger()
	.withSchedule(
		DailyTimeIntervalScheduleBuilder
		.dailyTimeIntervalSchedule()
		.withIntervalInMinutes(72)
		.startingDailyAt(TimeOfDay.hourMinuteAndSecondOfDay(8, 0, 0))
		.endingDailyAt(TimeOfDay.hourMinuteAndSecondOfDay(17, 0, 0))
		.onMondayThroughFriday())
	.build();
scheduler.scheduleJob(job, trigger);
