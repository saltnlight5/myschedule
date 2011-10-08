logger.info("Plugin shutdown");

importClass(Packages.integration.myschedule.quartz.extra.ScriptingSchedulerPluginIT);
ScriptingSchedulerPluginIT.writeResult('shutdown: ' + new Date() + '\n');
