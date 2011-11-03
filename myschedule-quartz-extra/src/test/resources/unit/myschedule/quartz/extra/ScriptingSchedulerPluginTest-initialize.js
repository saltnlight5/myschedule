//logger.info("Current classpath " + java.lang.System.getProperty("java.class.path"));
//logger.info("Class: " + Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT)
logger.info("Plugin initialize");

importClass(Packages.unit.myschedule.quartz.extra.ScriptingSchedulerPluginTest);
ScriptingSchedulerPluginTest.RESULT_FILE.writeLine('name: ' + schedulerPlugin.getName());
ScriptingSchedulerPluginTest.RESULT_FILE.writeLine('initialize: ' + new Date());
