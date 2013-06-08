// JavaScript Examples

// Create some durable jobs without triggers.
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail("durableJob1", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail("durableJob2", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail("durableJob3", "DEFAULT", LoggerJob, true, null);
scheduler.addJob(job, false);


// Create a job that takes 15 secs to run.
dataMap = HashMap();
dataMap.put('ScriptEngineName', 'JavaScript');
dataMap.put('ScriptText',
		'logger.info("I take 15 secs to run...");\n' +
		'Packages.java.lang.Thread.sleep(15000);\n' +
		'logger.info("I am done.");\n');
scheduler.scheduleSimpleJob("15secJob", -1, 60 * 60 * 1000, ScriptingJob,
		dataMap,
		Packages.java.util.Date(System.currentTimeMillis() + 20 * 1000));
