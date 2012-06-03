importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
scheduler.scheduleSimpleJob("hourlyJob1", -1, 60 * 60 * 1000, LoggerJob);