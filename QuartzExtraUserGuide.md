We have packaged a standalone jar that you may download and use it in additional to [Quartz distribution](http://quartz-scheduler.org/downloads). If you are a Java programmer, and if you familiar with Quartz scheduler, then all you need is our JavadocApi to get started. If you want more details, then read on.

Note that this quartz-extra jar has already been packaged into the `MySchedule` war! So if you are using the war deployment, you don't need to download this jar separately. You may find this documentation useful however.

This quartz-extra jar will bring you these main features:



# `SchedulerMain` #
This is a command line tool that let you start a quartz with any configuration and run as a server in foreground process.

A Linux example (assumed you unpacked latest Quartz distribution in `/opt/quartz`):
```
$ export CP=/opt/quartz/*:/opt/quartz/lib/*:myschedule-quartz-extra.jar
$ java -cp $CP myschedule.quartz.extra.SchedulerMain quartz.properties
```

See quartz website on how to configure your `quartz.properties`. You can hit `CTRL+C` to shutdown the scheduler.

If you just want to run the scheduler for fixed number of milliseconds, and then automatically exit, you may use the `-DSchedulerMain.TimeOut=60000` system properties. (Eg: perhaps you just want to start your scheduler with some plugin to be executed, and then want to exit.)

# `SchedulerTemplate` #
The main entry to Quartz API is the `org.quartz.Scheduler`, but using this class can be heavy at times. First is that all methods throws **checked** `SchedulerException`, so it makes caller unfriendly. Second is that the number required classes needed (import) in order to schedule a simple job is too much. So we provided `SchedulerTemplate` that solved these issues. We properly wrapped all `org.quartz.Scheduler` methods and throw **unchecked** `QuartzRuntimeException`, and we provide many easy methods to get job scheduled.

Compare these between `SchedulerTemplate` and Quartz's plain `Scheduler` usage on creating a minutely job that runs total of three times. (Noticed that Quartz use **2** as repeatCount.)
<table border='1'>

<tr valign='top'>
<td>
<pre><code>import myschedule.quartz.extra.SchedulerTemplate;<br>
SchedulerTemplate st = new SchedulerTemplate("my-quartz.properties");<br>
st.scheduleSimpleJob("test", 3, 60000, TestJob.class);<br>
</code></pre>
</td>
<td>
<pre><code>import static org.quartz.JobBuilder.newJob;<br>
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;<br>
import static org.quartz.TriggerBuilder.newTrigger;<br>
import org.quartz.JobDetail;<br>
import org.quartz.Scheduler;<br>
import org.quartz.SchedulerException;<br>
import org.quartz.SimpleTrigger;<br>
import org.quartz.impl.StdSchedulerFactory;<br>
public class DemoTest {<br>
  @Test<br>
  public void test() throws Exception {<br>
    try {<br>
      StdSchedulerFactory factory = new StdSchedulerFactory("my-quartz.properties");<br>
      Scheduler scheduler = factory.getScheduler();<br>
      JobDetail jobDetail = newJob(TestJob.class).withIdentity("test").build();<br>
      SimpleTrigger trigger = newTrigger()<br>
        .withIdentity("test")<br>
        .withSchedule(<br>
          simpleSchedule()<br>
          .withRepeatCount(2)									      <br>
          .withIntervalInMilliseconds(60000))<br>
        .build();<br>
      scheduler.scheduleJob(jobDetail, trigger);<br>
    } catch (SchedulerException e) {<br>
      throw new RuntimeExcpetion(e);<br>
    }<br>
  }<br>
}<br>
</code></pre>
</td>
</tr>

</table>

You can see JavadocApi for complete list of methods.

# `JdbcSchedulerHistoryPlugin` #
This plugin will record all your scheduler activities in DB such as when scheduler started, shutdown, a job triggered and completed executing. This plugin requires you to have DB persistence configured for Quartz, and you must create an extra table schema.

See the API doc for more [configuration](http://static.myschedule.googlecode.com/hg/myschedule-2.x/apidocs/myschedule/quartz/extra/JdbcSchedulerHistoryPlugin.html) details

# `ScriptingSchedulerPlugin` #
The Quartz has a `org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin` plugin that can load jobs using a xml definitions file. I find this work okay, but it doesn't get updated with all the available Trigger types available in the library. Also the xml schema validation seems to be too strict too.

So we provided a new improved `myschedule.quartz.extra.ScriptingSchedulerPlugin` plugin. This plugin can take any script files and executed, and you are free to do anything in a scripting file instead of just loading jobs through XML. The point is that it let you configure and manipulate your scheduler without having to compile Java code!

JDK6 or higher comes with `JavaScript` script engine built-in! You may also have the option of switch to any popular ones such as `Groovy` or `JRuby` by simply add their jars in your classpath.

Here is an example configuration for this plugin in `quartz.properties`:
```
# Scripting plugin
org.quartz.plugin.MyScriptingPlugin.class = myschedule.quartz.extra.ScriptingSchedulerPlugin
org.quartz.plugin.MyScriptingPlugin.scriptEngineName = JavaScript
org.quartz.plugin.MyScriptingPlugin.initializeScript = my-initialize.js
org.quartz.plugin.MyScriptingPlugin.startScript = my-start.js
org.quartz.plugin.MyScriptingPlugin.shutdownScript = my-shutdown.js
```

You can provide just one of the script if you don't care about executing something in that state.

# `ScriptingJob` #
Similar idea to the scripting plugin, we also provide a flexible `myschedule.quartz.extra.job.ScriptingJob` to let you use in Quartz. You can execute any script text or file in the job by configuring using the data map. See javadoc for more information on what keys to use.

Here is an example usage:
```
SchedulerTemplate st = new SchedulerTemplate();
JobDetail job = SchedulerTemplate.createJobDetail("MyScriptingJobTest", ScriptingJob.class);
job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "print('I am a script running in scheduler.');");
Trigger trigger = SchedulerTemplate .createSimpleTrigger("MyScriptingJobTest");
st.scheduleJob(job, trigger);
st.startAndShutdown(60000);
```

This is a very powerful, reusable Job, because it allow you deploy Quartz and run any job without having to restart server to load new Job class definitions!

# `OsCommandJob` #
The Quartz comes with a `org.quartz.job.NativeJob` to run external command. But we have a better feature one, and we named `myschedule.quartz.extra.job.OsCommandJob`. This job is Quartz interruptable! And you can use an optional "Timeout" feature to ensure your external command will not block your system.

You will find examples in our latest  [OsCommandJobTest](http://code.google.com/p/myschedule/source/browse/myschedule-quartz-extra/src/test/java/unit/myschedule/quartz/extra/job/OsCommandJobTest.java).