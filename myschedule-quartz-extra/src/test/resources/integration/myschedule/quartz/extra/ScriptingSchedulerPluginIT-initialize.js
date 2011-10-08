//logger.info("Current classpath " + java.lang.System.getProperty("java.class.path"));
//logger.info("Class: " + Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT)
logger.info("Plugin initialize");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT);
ScriptingSchedulerPluginIT.RESULT_FILE.writeLine('name: ' + schedulerPlugin.getName());
ScriptingSchedulerPluginIT.RESULT_FILE.writeLine('initialize: ' + new Date());
