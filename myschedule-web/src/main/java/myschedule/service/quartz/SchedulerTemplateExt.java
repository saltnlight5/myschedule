package myschedule.service.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import myschedule.job.GroovyScriptJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * We separated these methods out of SchedulerTemplate so that other class be be specific to Quartz only.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SchedulerTemplateExt extends SchedulerTemplate {
	public SchedulerTemplateExt() {
	}
	
	public SchedulerTemplateExt(Scheduler scheduler) {
		super(scheduler);
	}

	public Date scheduleGroovyCronJob(String name, String cron, Date startTime, String groovyText) {
		JobDetail job = createGroovyJob(name, groovyText);
		Trigger trigger = createCronTrigger(name, cron, startTime);
		return scheduleJob(job, trigger);
	}
	
	public Date scheduleGroovyFileCronJob(String name, String cron, Date startTime, String fileName) {
		JobDetail job = createGroovyFileJob(name, fileName);
		Trigger trigger = createCronTrigger(name, cron, startTime);
		return scheduleJob(job, trigger);
	}
	
	public static JobDetail createGroovyFileJob(String name, String fileName) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(GroovyScriptJob.GROOVY_SCRIPT_FILE_KEY, fileName);
		return SchedulerTemplate.createJobDetail(name, GroovyScriptJob.class, data);
	}
	
	public static JobDetail createGroovyJob(String name, String groovyText) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(GroovyScriptJob.GROOVY_SCRIPT_TEXT_KEY, groovyText);
		return SchedulerTemplate.createJobDetail(name, GroovyScriptJob.class, data);
	}
}
