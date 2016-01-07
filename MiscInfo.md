# How to see Winstone temporary directory used when expanding war file #

If you are running embedded Winston between MySchedule-3.2.x and MySchedule-3.1.x then sometimes you might see error like this:

```
Caused by: java.lang.IncompatibleClassChangeError: Found class org.quartz.Trigger, but interface was expected
	at myschedule.web.ui.JobsWithTriggersContent.reloadTableContent(JobsWithTriggersContent.java:232)
	at myschedule.web.ui.JobsWithTriggersContent.initJobsTable(JobsWithTriggersContent.java:221)
...
```

This is because Winstone won't auto clean up their previous run tmp folder content. Then you have to manually clear out this directory to avoid wrong jars loaded by Winstone classloader. But If you are running on MacOSX, this directory can be tricky to find. I find it by running this groovy code

```
bash> groovy -e 'println(System.getProperty("java.io.tmpdir"))'
/var/folders/ws/mgb647r92x360wp5tn9lb0q40000gn/T/

# now you can wipe the Winstone temp folder clean
bash> rm -r /var/folders/ws/mgb647r92x360wp5tn9lb0q40000gn/T/winstone*
```

# How to write a custom java.net.URL protocol for 'classpath:' #
```
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ClasspathURLStreamHandler extends URLStreamHandler {
        
        @Override
        public URLConnection openConnection(URL url) throws IOException {
                String protocol = url.getProtocol();
                if ("classpath".equals(protocol)) {
                        String path = url.getPath();
                        URL resUrl = getClassLoader().getResource(path);
                        return resUrl.openConnection();
                } else {
                        // Use default JDK url impl.
                        return url.openConnection();
                }
        }

        private ClassLoader getClassLoader() {
                return Thread.currentThread().getContextClassLoader();
        }
}

// Now to use it, do this:
URL url = new URL(null, "classpath:atest/myapp/config.properties", new ClasspathURLStreamHandler());
```

# A demo of Quartz API using Fantom language #
```
// export CLASSPATH="C:/apps/quartz-2.1.0/*;C:/apps/quartz-2.1.0/lib/*"
using [java]org.quartz
using [java]org.quartz.impl
using [java]org.quartz.jobs

class Quartz
{
	static Void main()
	{
		scheduler := StdSchedulerFactory.getDefaultScheduler()
		echo("Scheduler instance created $scheduler")
		
		job := JobBuilder.newJob(NoOpJob#->toClass).withIdentity("testJob").build()
		trigger := TriggerBuilder.newTrigger().withIdentity("testJob").build()
		scheduler.scheduleJob(job, trigger)
		
		echo("Going to sleep and let scheduler does its work.")
		concurrent::Actor.sleep(5sec)
		scheduler.shutdown()
		
		echo("Done.")
	}
}
```
# A Self Ping Heroku Job in Groovy #
```
import org.quartz.*
import myschedule.quartz.extra.job.*
job = JobBuilder
	.newJob(ScriptingJob.class)
	.withIdentity("HerokuSelfPingJob")
	.usingJobData("ScriptEngineName", "Groovy")
	.usingJobData("ScriptText", "assert new URL('http://stormy-flower-6956.herokuapp.com/main/dashboard/list').text.contains('ZemianScheduler_\$_NON_CLUSTERED') == true")
	.build()
trigger = TriggerBuilder
	.newTrigger()
        .withIdentity("HerokuSelfPingJob")
	.withSchedule(
		SimpleScheduleBuilder.repeatHourlyForever())
	.build()
scheduler.scheduleJob(job, trigger)
```

# Scripting Java with `JavaScript` #
```
// Print all System properties in sorted order
importClass(java.lang.System);
importClass(java.util.ArrayList);
importClass(java.util.Collections);
var props = System.getProperties();
var names = ArrayList();
names.addAll(props.stringPropertyNames());
Collections.sort(names);
for (var i =0; i < names.size(); i++) {
  var name = names.get(i);
  output.println(name + ": " + System.getProperty(name));
}

// Print all Environment varaibles in sorted order
importClass(java.lang.System);
importClass(java.util.ArrayList);
importClass(java.util.Collections);
var envMap = System.getenv();
var names = ArrayList();
names.addAll(envMap.keySet());
Collections.sort(names);
for (var i =0; i < names.size(); i++) {
  var name = names.get(i);
  output.println(name + ": " + envMap.get(name));
}

```

# MySQL Tips #
Setup new database and user
```
create database quartz2;
create user 'quartz2'@'localhost' identified by 'quartz2123';
grant all privileges on quartz2.* to 'quartz2'@'localhost';
```

# Groovy Tips #
Using Groovy SQL:
```
import groovy.sql.Sql
sql = Sql.newInstance('jdbc:mysql://localhost:3306/quartz2', 'quartz2', 'quartz2123', 'com.mysql.jdbc.Driver')
sql.eachRow('select * from qrtz_triggers'){ row -> println(row) }
```

Or Using a `DataSource`:
```
import javax.naming.*
ctx = new InitialContext()
ds = ctx.lookup('quartz2')

import groovy.sql.Sql
sql = new Sql(ds)
sql.eachRow('select * from qrtz_triggers'){ row -> println(row) }
```

# OracleXE Tips #

To change the default port of http://localhost:8080/apex, run:
`exec dbms_xdb.sethttpport('8888');`

A typical JDBC URL format:
```
jdbc:oracle:thin:@[HOST][:PORT]:SID
or
jdbc:oracle:thin:@//[HOST][:PORT]/SERVICE

Eg: jdbc:oracle:thin:@localhost:1521:XE
```

# Apache Derby DB (JavaDB) #
```
-- DerbyDB + Quartz Quick Guide:
-- * Derby comes with Oracle JDK! For Java6, it default install into C:/Program Files/Sun/JavaDB on Windows.
-- 1. Create a derby.properties file under JavaDB directory, and have the following:
--    derby.connection.requireAuthentication = true
--    derby.authentication.provider = BUILTIN
--    derby.user.quartz2=quartz2123
-- 2. Start the DB server by running bin/startNetworkServer script.
-- 3. On a new terminal, run bin/ij tool to bring up an SQL prompt, then run:
--    connect 'jdbc:derby://localhost:1527/quartz2;user=quartz2;password=quartz2123;create=true';
--    run 'quartz/docs/dbTables/tables_derby.sql';
-- Now in quartz.properties, you may use these properties:
--    org.quartz.dataSource.quartzDataSource.driver = org.apache.derby.jdbc.ClientDriver
--    org.quartz.dataSource.quartzDataSource.URL = jdbc:derby://localhost:1527/quartz2
--    org.quartz.dataSource.quartzDataSource.user = quartz2
--    org.quartz.dataSource.quartzDataSource.password = quartz2123
```

---


# Other `MySchedule` Info #

`MySchedule` is an open source web application dashboard for Quartz scheduler. You can try it out by download the latest [myschedule package](http://code.google.com/p/myschedule/downloads/list) and see the [UserGuide](http://code.google.com/p/myschedule/wiki/MyScheduleUserGuide) to get started.

The [Quartz Scheduler](http://quartz-scheduler.org) is a open source Java library for scheduling services. It has full CRON expression feature and more. The `MySchedule` webapp will give you a fully working scheduler out of the box that runs on any Servlet container.


---

There is also a separate `experiment` Hg repository in here that contains few useful example codes.

[quartz-1.8-experiment](http://code.google.com/p/myschedule/source/browse/quartz-1.8-experiment?repo=experiment) - A Maven/Java based project that explore Quartz 1.8.x branch. There are many examples and configuration properties files that demonstrate various quartz configuration, such as running with database, RMI, and clustering etc. There is also some scripts runners that starts off demos in command line.


[quartz-experiment](http://code.google.com/p/myschedule/source/browse/quartz-experiment?repo=experiment) - A Maven/Java based project that explore Quartz 2.0.x branch. Very similar to above, but using the latest API.

You may get all these by going here http://code.google.com/p/myschedule/source/checkout?repo=experiment.


---


I have presented Quartz with some sample codes above in Java User Group at Orlando, FL on 5/29/2011. You may see the power point slides here: http://code.google.com/p/myschedule/source/browse/jug-quartz/JUG-QuartzScheduler.pptx?repo=experiment