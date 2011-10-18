logger.info("Plugin shutdown");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginTest);
ScriptingSchedulerPluginTest.RESULT_FILE.writeLine('shutdown: ' + new Date());
