// Create 100 jobs
import org.quartz.*
100.times { i ->
	name = 'GroovyJob_every_min' + i
	jobDetail = new JobDetail(name, 'DEFAULT', myschedule.job.sample.SimpleJob.class)
	trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println('Job scheduled successfully: ' + jobDetail.fullName)
}

// Create 10 jobs that has durability set.
import org.quartz.*
100.times { i ->
	name = 'GroovyDurableJob_every_min' + i
	jobDetail = new JobDetail(name, 'DEFAULT', myschedule.job.sample.SimpleJob.class)
	jobDetail.setDurability(true)
	trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println('Job scheduled successfully: ' + jobDetail.fullName)
}

// Create 1 job, 100+1 triggers.
import org.quartz.*
// Generate jobs: every_hour
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
	name = 'GroovyJob_every_hour' + i
	trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
	trigger.setJobName('every_hour')
	trigger.setJobGroup('DEFAULT')
	quartzScheduler.scheduleJob(trigger)
	webOut.println('Trigger only added successfully: ' + trigger.fullName + ', job=' + jobDetail.fullName)
}