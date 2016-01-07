

# What is `MySchedule`? #

A Java based web application for managing Quartz Schedulers.

When you dive in deeper into `MySchedule`, you would see that it's a complete, standalone web application that provide UI to manage Quartz features. It also provides some Quartz extensions such as plugins and reusable jobs.

# I installed `MySchedule`, now what? #
Well, next thing is you should go create a Quartz scheduler instance by create a quart.properties contents using one of `MySchedule` UI screen. There many examples you may choose already. After you have a scheduler, you can navigate to it and start scheduling new jobs.

Next you should write your Quartz Job that does whatever you what, or use ones that provided to you already by `MySchedule` or Quartz system. `MySchedule` gives you a `ScriptingJob` that you don't have to compile code if that suite your. See documentation on usage.

Next you can schedule your job into Quartz using one of its Trigger type (eg: `SimpleTrigger` or `CronTrigger` etc.) You can do this in two ways in `MySchedule`: use the XML loader or Scripting UI.

If you done these properly, you should see the jobs on your `MySchedule` job list, and it will run according to your schedule.

# How many jobs can I add to `MySchedule`? #
It would depend on your hardware and OS capability. The Quartz can scale and can use database with clustering feature. You can easily schedule thousands of jobs running without much trouble on a typical machine. You would have to do your own judgement and measurement with what you need.

# Can I use `MySchedule` in my company? #
Sure, as long as you accept the License usage, which allow you to do many things.

# Where can I learn more about Quartz itself? #
See http://quartz-scheduler.org

You may ask Quartz related questions on [their forums](http://forums.terracotta.org/forums/forums/show/17.page)

# Can I integrate `MySchedule` into my own web application? #
The MySchedule has two parts: the quartz-extra jar and the war webapp.

For quartz-extra jar, you may use it just as any other jar in your java application. It provides extra reusable Quartz Jobs and Plugins for you to use in your custom Quartz usage.

For the war package, it's more standalone webapp. Normally, you can not integrate a completed web application that already has it's own UI screen, unless you willing to use it as it, or restyleing all the looks. So if this works for you, feel free. (See also on license usage.)

If you already have your own web application and UI, you would benefit more to use Quartz as it, a Java library in your own application. You are still free to use MySchedule's quartz extra jar though.

The `MySchedule` is an standalone web application that provides an UI to manage quartz. There is no restriction on what a user can do here, so it would run as admin privilege app! It would be more like a job server running separately from your other applications. If this is what you want, then schudule the job in MySchedule instead. Otherwise you would use quartz as embedded library.

# How can I manage remote Quartz using `MySchedule` ? #

You can enable JMX for Quartz, but `MySchedule` itself doesn't provide JMX client. You use standard JMX client such as `$JAVA_HOME/bin/jconsole` to connect and manage remotely. To enable JMX in Quartz, you may see an example when you run `MySchedule` and click the Create scheduler config. Eg
```
org.quartz.scheduler.jmx.export = true
```

Another way to manage quartz remotely is to enable RMI in Quartz. If you use this, you basically run one instance of Quartz as RMI server, and then you can create second Quartz instance as RMI client. These two can talk remotely via a TCP port. If you do this, then you can use `MySchedule` create either the server or client. If you create the client, then you are essentially creating a proxy (view) into the remote server!

Again, you will see an example of scheduler config when you use our MySchedule to create the scheduler.

See http://quartz-scheduler.org/documentation/quartz-2.x/configuration/ConfigRMI for more.

# How can I troubleshoot or see what jobs has been executed? #
First option is use the logger! You should turn on logging on `org.quartz` package to INFO, or even DEBUG level to see all the Quartz activities. The `MySchedule` also produce logging, and you can see them by enable `myschedule` package.

Second option is use some Quartz plugin or Listeners that record the jobs activities. The `MySchedule` comes with a `JdbcSchedulerHistoryPlugin` that you can even record them into database! Seee documentation for more details.

# Why not just use Unix Crontab? #
There are many reasons why you want a Java based scheduling system. I would list few here:
  * Quartz can run in clustered and support much larger amount of jobs.
  * Quartz jobs are recoverable from database and recordable.
  * Quartz has a very rich API to interact with scheduler.
  * Quartz let you run job that's written in Java much more efficient.
  * Quartz can control the threads used to run jobs.
  * Quartz has much more different type of scheduling besides just cron.
  * Quartz runs on any OS that supports the Java JVM.

Note that Quartz cron expression supports an extra 'second' field, while Unix cron would have only down to 'minute' as smallest unit!

# Where to get news? #
There is a project feed: http://code.google.com/p/myschedule/feeds

Also, I keep a blog here http://saltnlight5.blogspot.com

# How can I help with `MySchedule`? #
We can always use feedback and bug reports based on your experience with `MySchedule`. If you are developers, then feel free to clone the Hg repository and send me pull request if you got fixes or patches.

We can especially use some help on making the UI more prettier. So if you are good at this area, feel free to contact us!

We would always appreciate and welcome all comments and contribution!