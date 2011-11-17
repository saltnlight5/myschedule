importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.org.quartz.JobBuilder);
importClass(Packages.org.quartz.TriggerBuilder);
importClass(Packages.org.quartz.CronScheduleBuilder);
var job = JobBuilder
  .newJob(LoggerJob)
  .withIdentity("cronJob")
  .build();
var trigger = TriggerBuilder
  .newTrigger()
  .withIdentity("cronJob")
  .withSchedule(
    CronScheduleBuilder.cronSchedule("0 0 * ? * MON-FRI"))
  .build();
scheduler.scheduleJob(job, trigger);