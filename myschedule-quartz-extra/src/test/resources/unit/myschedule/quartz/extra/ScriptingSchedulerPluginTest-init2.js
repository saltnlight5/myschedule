importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
scheduler.scheduleSimpleJob("hourlyJob2", -1, 60 * 60 * 1000, LoggerJob);