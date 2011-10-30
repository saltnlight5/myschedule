// JavaScript Examples

// Create some durable jobs without triggers.
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail(JobKey.jobKey("durableJob1"), LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail(JobKey.jobKey("durableJob2"), LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail(JobKey.jobKey("durableJob3"), LoggerJob, true, null);
scheduler.addJob(job, false);
