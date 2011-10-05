import org.quartz.*
import myschedule.job.sample.*

/*
// Delete all jobs first!!!!
count = 0
schedulerTemplate.jobDetails.each{ job -> 
  schedulerTemplate.deleteJob(job.name, job.group)
  webOut.println("job deleted: " + job.name)
  count += 1
}
webOut.println(count + " jobs deleted.")
*/

// Generate jobs: every_christmas_morning
trigger = new CronTrigger('every_christmas', 'DEFAULT', '0 0 0 25 DEC ?')
quartzScheduler.scheduleJob(jobDetail, trigger)
webOut.println("job scheduled " + jobDetail.fullName)

// Generate jobs: every_sec_for_1_min
(1 .. 2).each { i -> 
	name = 'every_sec_for_1_min' + i
	startTime = new Date(System.currentTimeMillis() + 1 * 60 * 1000) // Starts with 1 min delay.
	endTime = new Date(startTime.getTime() + 1 * 60 * 1000) // 1 min later.
	repeatCount = 0
	repeatInterval = 0
	trigger = new SimpleTrigger(name, startTime, endTime, repeatCount, repeatInterval)
	jobDetail = new JobDetail(name, SimpleJob.class)
	quartzScheduler.scheduleJob(jobDetail, trigger) 
	webOut.println("job scheduled " + jobDetail.fullName)
}

// Generate jobs: onetime_start_in_30_secs
(1 .. 10).each { i -> 
	name = 'onetime_start_in_30_secs' + i
	startTime = new Date(System.currentTimeMillis() + 30000) // Starts with 30 secs delay.
	endTime = null
	repeatCount = 0
	repeatInterval = 0
	trigger = new SimpleTrigger(name, startTime, endTime, repeatCount, repeatInterval)
	jobDetail = new JobDetail(name, SimpleJob.class)
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println("job scheduled " + jobDetail.fullName)
}

// Generate jobs: every_min
(1 .. 10).each { i -> 
	name = 'every_min' + i
	startTime = new Date()
	endTime = null
	repeatCount = -1
	repeatInterval = 1 * 60 * 1000
	trigger = new SimpleTrigger(name, startTime, endTime, repeatCount, repeatInterval)
	jobDetail = new JobDetail(name, SimpleJob.class)
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println("job scheduled " + jobDetail.fullName)
}

// Generate jobs: every_hour_with_endtime
(1 .. 10).each { i -> 
	name = 'every_hour_with_endtime' + i
	startTime = new Date()
	endTime = new Date(startTime.getTime() + 24 * 60 * 60 * 1000) // 24 hours later.
	repeatCount = -1
	repeatInterval = 1 * 60 * 1000
	trigger = new SimpleTrigger(name, startTime, endTime, repeatCount, repeatInterval)
	jobDetail = new JobDetail(name, SimpleJob.class)
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println("job scheduled " + jobDetail.fullName)
}

// Generate jobs: every_day_at_0750AM
(1 .. 10).each { i -> 
	name = 'every_day_at_0750AM' + i
	cron = '0 50 7 * * ?'
	trigger = new CronTrigger(name, 'DEFAULT', cron)
	jobDetail = new JobDetail(name, SimpleJob.class)
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println("job scheduled " + jobDetail.fullName)
}

// Generate jobs: every_MON_to_FRI_at_5pm
(1 .. 10).each { i -> 
	name = 'every_MON_to_FRI_at_5pm' + i
	cron = '0 0 17 ? * MON-FRI'
	trigger = new CronTrigger(name, 'DEFAULT', cron)
	jobDetail = new JobDetail(name, SimpleJob.class)
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println("job scheduled " + jobDetail.fullName)
}

// Create a simple job that has no trigger associate it.
jobDetail = new JobDetail('job_with_trigger', SimpleJob.class)
jobDetail.setDurability(true)
quartzScheduler.addJob(jobDetail, false) // Do not replace existing job
webOut.println("job scheduled " + jobDetail.fullName)
