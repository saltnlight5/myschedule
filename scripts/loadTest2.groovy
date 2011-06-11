// Create 100 jobs
import org.quartz.*
100.times { i ->
	name = 'GroovyJob' + (System.currentTimeMillis() + i)
	jobDetail = new JobDetail(name, 'DEFAULT', myschedule.job.sample.SimpleJob.class)
	trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println('Job scheduled successfully: ' + jobDetail.fullName)
}

// Create 1 job, 100+1 triggers.
import org.quartz.*
// Generate jobs: every_hour_with_endtime
name = 'every_hour'
startTime = new Date()
endTime = null
repeatCount = -1
repeatInterval = 1 * 60 * 1000
trigger = new SimpleTrigger(name, startTime, endTime, repeatCount, repeatInterval)
jobDetail = new JobDetail(name, myschedule.job.sample.SimpleJob.class)
quartzScheduler.scheduleJob(jobDetail, trigger)
webOut.println("job scheduled " + jobDetail.fullName)	
100.times { i ->
	name = 'GroovyJob' + (System.currentTimeMillis() + i)
	trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
	trigger.setJobName('every_hour')
	trigger.setJobGroup('DEFAULT')
	quartzScheduler.scheduleJob(trigger)
	webOut.println('Trigger only added successfully: ' + trigger.fullName + ', job=' + jobDetail.fullName)
}