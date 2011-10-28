// Create a new job to the scheduler that runs every MON-FRI at 8am.
importPackage(Packages.myschedule.quartz.extra.job);
importPackage(Packages.java.util);
var dataMap = new HashMap();
dataMap.put('ScriptEngineName', 'JavaScript');
dataMap.put('ScriptText', 'logger.info("It will take me a min to wake up."); sleep(60000);');
var nextFireTime = scheduler.scheduleCronJob('MyDailyJob', '0 0 8 ? * MON-FRI', ScriptingJob, dataMap);
webOut.println('MyDailyJob has been scheduled. Next fire time: ' + nextFireTime);