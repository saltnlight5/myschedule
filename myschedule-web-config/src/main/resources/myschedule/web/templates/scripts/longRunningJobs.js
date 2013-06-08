// JavaScript Examples

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
