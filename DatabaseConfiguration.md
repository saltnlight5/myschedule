The `MySchedule` webapp is simply a dashboard to manage and control a Quartz scheduler instance. The webapp itself doesn't require or need a database. But the Quartz scheduler system does provide full features to store the job data in either In Memory (which is by default), or into a database so that your jobs can survive a server restart.

You should always check the Quartz website to see how to properly configure and setup database, especially with Clustering features.

Note: If you point to a `quartz.properites` that uses Database, ensure you have the right JDBC driver in your server classpath. Also if you are using specialized database that Quartz has support for, ensure the optional quartz jar are also added into your classpath as well (eg: The quartz-oracle.jar). See http://quartz-scheduler.org/documentation/quartz-2.1.x/configuration/ConfigJobStoreTX for more details. The default `MySchedule` package do not comes with any JDBC driver, so you you need to provide your own to server or webapp classpath.

# Example: Setting up `MySQL` Database with Quartz #
Here is a small tutorial on how to setup database tables with `MySQL` database and Quartz configuration.

Step 1: Create database user and schema name for Quartz

```
# Login to mysql as root, it should prompt you for password.
$ mysql -u root

sql> create database quartz2;
sql> create user 'quartz2'@'localhost' identified by 'quartz2123';
sql> grant all privileges on quartz2.* to 'quartz2'@'localhost';
sql> exit;
```

Step2: Create the schema tables for Quartz

  1. Download latest Quartz-2.x distribution package from http://quartz-sheduler.org
  1. Unzip it into a direcotry. (Eg: `/c/apps/quartz-2.1.0`)
  1. Create tables by running
```
     $ cd /c/apps/quartz-2.1.0
     $ mysql -u quartz2 < docs/dbTables/tables_mysql.sql
```

Step3: Create `quartz.properites` that uses the `MySQL` database. You may use the `MySchedule` web application to create this scheduler config properties.
```
org.quartz.scheduler.skipUpdateCheck = true
org.quartz.scheduler.instanceName = QuartzScheduler
org.quartz.scheduler.jobFactory.class = org.quartz.simpl.SimpleJobFactory
org.quartz.scheduler.instanceId = NON_CLUSTERED
org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.dataSource = quartzDataSource
org.quartz.jobStore.tablePrefix = QRTZ_
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount = 5

# Persistence Job Store - with Oracle database
org.quartz.dataSource.quartzDataSource.driver = com.mysql.jdbc.Driver
org.quartz.dataSource.quartzDataSource.URL = jdbc:mysql://localhost:3306/quartz2
org.quartz.dataSource.quartzDataSource.user = quartz2
org.quartz.dataSource.quartzDataSource.password = quartz2123
org.quartz.dataSource.quartzDataSource.maxConnections = 8
```

NOTE: Remeber to add `mysql.jar` JDBC driver jar into your web server container. (Eg: for Tomcat server, you would add it to `$TOMCAT_HOME/lib` directory.)

# Example: Oracle config #
```
org.quartz.dataSource.quartzDataSource.driver = oracle.jdbc.driver.OracleDriver
org.quartz.dataSource.quartzDataSource.URL = jdbc:oracle:thin:@localhost:1521:XE
```