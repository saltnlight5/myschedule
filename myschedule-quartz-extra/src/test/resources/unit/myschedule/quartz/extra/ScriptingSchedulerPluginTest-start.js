logger.info("Plugin start");

importClass(Packages.unit.myschedule.quartz.extra.ScriptingSchedulerPluginTest);
ScriptingSchedulerPluginTest.RESULT_FILE.appendLine('start: ' + new Date());
