logger.info("Plugin start");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT);
ScriptingSchedulerPluginIT.writeResult('start: ' + new Date() + '\n');
