//logger.info("Current classpath " + java.lang.System.getProperty("java.class.path"));
//logger.info("Class: " + Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT)
logger.info("Plugin initialize");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT);
ScriptingSchedulerPluginIT.writeResult('name: ' + schedulerPlugin.getName() + '\n');
ScriptingSchedulerPluginIT.writeResult('initialize: ' + new Date() + '\n');
