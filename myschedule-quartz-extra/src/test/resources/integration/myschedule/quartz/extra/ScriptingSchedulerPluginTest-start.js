logger.info("Plugin start");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginTest);
ScriptingSchedulerPluginTest.RESULT_FILE.writeLine('start: ' + new Date());
