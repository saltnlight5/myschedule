// JavaScript Examples

/*
 * 
You may include these and other js file for testing in the quartz.properties like this:

# Quartz Extra - ScriptingSchedulerPlugin
org.quartz.plugin.MyScriptingPlugin.class = myschedule.quartz.extra.ScriptingSchedulerPlugin
org.quartz.plugin.MyScriptingPlugin.scriptEngineName = JavaScript
org.quartz.plugin.MyScriptingPlugin.initializeScript = \
classpath:myschedule/script/examples/simpleJobs.js,\
classpath:myschedule/script/examples/cronJobs.js,\
classpath:myschedule/script/examples/advanceJobs.js,\
classpath:myschedule/script/examples/calendars.js,\
classpath:myschedule/script/examples/jobsWithoutTriggers.js,\
classpath:myschedule/script/examples/longRunningJobs.js

 */

// Create some durable jobs without triggers.
importClass(Packages.myschedule.quartz.extra.job.LoggerJob);
importPackage(Packages.org.quartz);
job = scheduler.createJobDetail(JobKey.jobKey("durableJob1"), LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail(JobKey.jobKey("durableJob2"), LoggerJob, true, null);
scheduler.addJob(job, false);

job = scheduler.createJobDetail(JobKey.jobKey("durableJob3"), LoggerJob, true, null);
scheduler.addJob(job, false);
