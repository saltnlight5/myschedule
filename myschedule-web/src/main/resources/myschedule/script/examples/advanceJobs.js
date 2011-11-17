//Using CalendarIntervalTrigger
//schedule a job that runs every 3 months
//=========================================
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.org.quartz.JobBuilder);
importClass(Packages.org.quartz.TriggerBuilder);
importClass(Packages.org.quartz.CalendarIntervalScheduleBuilder);

var job = JobBuilder
    .newJob(LoggerJob)
    .withIdentity("calendarIntervalJob")
    .build();
var trigger = TriggerBuilder
    .newTrigger()
    .withIdentity("calendarIntervalJob")
    .withSchedule(
        CalendarIntervalScheduleBuilder
        .calendarIntervalSchedule()
        .withIntervalInMonths(3))
    .build();
scheduler.scheduleJob(job, trigger);

//Using DailyTimeIntervalTrigger
//schedule a job that runs every 72 mins MON-FRI from 8:00AM to 5:00PM
//======================================================================
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.org.quartz.JobBuilder);
importClass(Packages.org.quartz.TriggerBuilder);
importClass(Packages.org.quartz.DailyTimeIntervalScheduleBuilder);
importClass(Packages.org.quartz.TimeOfDay);

var job = JobBuilder
    .newJob(LoggerJob)
    .withIdentity("dailyTimeIntervalJob")
    .build();
var trigger = TriggerBuilder
    .newTrigger()
    .withIdentity("dailyTimeIntervalJob")
    .withSchedule(
        DailyTimeIntervalScheduleBuilder
        .dailyTimeIntervalSchedule()
        .withIntervalInMinutes(72)
        .startingDailyAt(TimeOfDay.hourMinuteAndSecondOfDay(8, 0, 0))
        .endingDailyAt(TimeOfDay.hourMinuteAndSecondOfDay(17, 0, 0))
        .onMondayThroughFriday())
    .build();
scheduler.scheduleJob(job, trigger);