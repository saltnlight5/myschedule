# Creating Java Job #
In Quartz, you write user custom job by simply implementing the `org.quartz.Job` interface. (eg: See [LoggerJob](http://code.google.com/p/myschedule/source/browse/myschedule-quartz-extra/src/main/java/myschedule/quartz/extra/job/LoggerJob.java)) You may add any of these classes in the classpath, and the `MySchedule` application will able to create a new job with it. Once you have the job class name, you simply have to create a `JobDetail` and a `Trigger` instances to schedule a job. Or you can use our `SchedulerTemplate` to simplify these steps into a single line.

Here is an exmple of simple Quartz-2.x job implemetation.
```
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MyJob implements Job {
  private static final Logger logger = LoggerFactory.getLogger(LoggerJob.class);
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    logger.info("My job is executing.");
  }
}
```

Please note that when adding new Job class using Java, you must make the new class definition available to the `MySchedule` web application, which mean you have to restart the application whenever you change the class. One way to add more Job classes is to unpack `myschedule.war` and add it to `WEB-INF/classes`. But depending which server you use, you may also do this without unpacking the `myschedule.war` file if the server supports dynamic classloading.

Also, before you writing your own job, check out what's come with [Quartz's built-in jobs](http://quartz-scheduler.org/api/2.1.0/org/quartz/jobs/package-summary.html). These are already available to you in the application by default.

Once you have a `Job` class defined, you can schedule it in two ways: using the XML Loader or the Scripting UI interface. The xml loader is a plugin that comes with Quartz, and `MySchedule` has created an UI to let you load it dynamically.

You may also add any jobs using the Scripting UI interface. You use any scripting language to access any of the Quart API dynamically. You are free to do any task you need to manage the scheduler.

Here is an Java snippet on how you schedule the `MyJob` job defined above:
```
import org.quartz.*;

// Inside a method where you have "scheduler" (`org.quartz.Scheduler`) 
// instance ready, then you can schedule the job like this:
JobDetail jobDetail = JobBuilder.
  newJob(MyJob.class).
  withIdentity("myJob").
  build();

SimpleTrigger trigger = TriggerBuilder.
  newTrigger().
  withIdentity("class").
  withSchedule(
    SimpleScheduleBuilder.
    simpleSchedule().
    withRepeatCount(2).
    withIntervalInMilliseconds(60000)).
  build();

scheduler.scheduleJob(jobDetail, trigger);
```

# Using the `myschedule.quartz.extra.job.ScriptingJob` #

The `MySchedule` web application also provides a solution for you to create custom job without having to write new Java Job class definition, which mean you don't need to restarting the webapp or server. By using the `myschedule.quartz.extra.job.ScriptingJob`, you can "script" any job you need, and yet you have the full power of programming on a language of your choice. By default the JDK6 or higher supports `JavaScript` as scripting language out fo the box!

Here is an example how you create a `ScriptpingJob`.
```
import static myschedule.quartz.extra.SchedulerTemplate.createJobDetail;
import static myschedule.quartz.extra.SchedulerTemplate.createSimpleTrigger;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.ScriptingJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;


// Inside a method where you have "scheduler" (`org.quartz.Scheduler`) 
// instance ready, then you can schedule the job like this:
JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "2 + 99;");
Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
scheduler.scheduleJob(job, trigger);
```

TIPS: You can also easily add other scripting engine such as Groovy by adding `groovy-all.jar` into server classpath (eg: `$TOMCAT_HOME/lib`).