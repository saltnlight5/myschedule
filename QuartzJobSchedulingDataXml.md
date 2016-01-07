Here is an example of Quartz's job-scheduling-data file you can load into scheduler.

Note: As of `quartz-1.8.5`, there are still bugs in the schema that not allow you to input some valid cron expression. I've submitted patches for some of it already. See [QTZ-146](http://jira.terracotta.org/jira/browse/QTZ-146). You can workaround this using the scripting to add the job though.

```
<?xml version='1.0' encoding='utf-8'?>
<job-scheduling-data 
  version="1.8" 
  xmlns="http://www.quartz-scheduler.org/xml/JobSchedulingData" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.quartz-scheduler.org/xml/JobSchedulingData http://www.quartz-scheduler.org/xml/job_scheduling_data_1_8.xsd">

	<schedule>
	
        <!-- Run this job every 1.5 sec forever -->
		<job>
			<name>my_job1</name>
			<group>DEFAULT</group>
			<job-class>myschedule.quartz.extra.job.LoggerJob</job-class>
		</job>
		<trigger>
			<simple>
				<name>my_job1</name>
				<group>DEFAULT</group>
				<job-name>my_job1</job-name>
				<job-group>DEFAULT</job-group>
				<repeat-count>-1</repeat-count>
				<repeat-interval>1500</repeat-interval>
			</simple>
		</trigger>
		
        <!-- Run this job every night at 4:30 am -->
		<job>
			<name>my_job2</name>
			<group>DEFAULT</group>
			<description>Just a example.</description>
			<job-class>quartz.experiment.SimpleJob</job-class>
			<job-data-map>
				<entry>
					<key>color</key>
					<value>RED</value>
				</entry>
			</job-data-map>
		</job>
		<trigger>
			<cron>
				<name>my_job2</name>
				<group>DEFAULT</group>
				<job-name>my_job2</job-name>
				<job-group>DEFAULT</job-group>
				<cron-expression>0 30 4 * * ?</cron-expression>
			</cron>
		</trigger>
		
	</schedule>
	
</job-scheduling-data>
```