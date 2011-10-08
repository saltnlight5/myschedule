logger.info("Plugin shutdown");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT);
ScriptingSchedulerPluginIT.RESULT_FILE.writeLine('shutdown: ' + new Date());
