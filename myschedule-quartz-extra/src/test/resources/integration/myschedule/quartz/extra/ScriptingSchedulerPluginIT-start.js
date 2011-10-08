logger.info("Plugin start");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT);
ScriptingSchedulerPluginIT.RESULT_FILE.writeLine('start: ' + new Date());
