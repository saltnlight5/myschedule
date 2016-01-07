# Misc tasks #

```
// Create durable job (without trigger)
job = org.quartz.JobBuilder
  .newJob(myschedule.quartz.extra.job.LoggerJob)
  .withIdentity("durableJob")
  .storeDurably()
  .build()
scheduler.addJob(job, true)
```

```
// Add listeners that logs actions in DEBUG level
manager = scheduler.getListenerManager()
manager.addJobListener(new myschedule.quartz.extra.SimpleJobListener())
manager.addTriggerListener(new myschedule.quartz.extra.SimpleTriggerListener())
manager.addSchedulerListener(new myschedule.quartz.extra.SimpleSchedulerListener())
```