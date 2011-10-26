logger.info("Plugin shutdown");

importClass(Packages.unit.myschedule.quartz.extra.ScriptingSchedulerPluginTest);
ScriptingSchedulerPluginTest.RESULT_FILE.writeLine('shutdown: ' + new Date());
