// JavaScript Examples

// Create some durable jobs without triggers.
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importClass(Packages.myschedule.quartz.extra.job.ScriptingJob);
importPackage(Packages.org.quartz);
importPackage(Packages.java.util);
job = scheduler.createJobDetail("durableJob1", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, true);

job = scheduler.createJobDetail("durableJob2", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, true);

job = scheduler.createJobDetail("durableJob3", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, true);

// Create a job that takes 15 secs to run.
dataMap = HashMap();
dataMap.put('ScriptEngineName', 'JavaScript');
dataMap.put('ScriptText',
		'logger.info("I take 15 secs to run...");\n' +
		'Packages.java.lang.Thread.sleep(15000);\n' +
		'logger.info("I am done.");\n');
scheduler.scheduleSimpleJob("15secJob", -1, 60 * 60 * 1000, ScriptingJob,
		dataMap,
		Packages.java.util.Date(java.lang.System.currentTimeMillis() + 20 * 1000));
