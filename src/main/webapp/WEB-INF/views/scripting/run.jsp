<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Scheduler Scripting</h1>
<p class="info">
This is for the power user who want to manipulate the runtime scheduler with 
<a href="http://groovy.codehaus.org">Groovy</a> scripting. You have "quartzScheduler"
variable (type of <code>org.quartz.Scheduler</code>) ready for your script to use. You
may query the scheduler, add, delete, or anything you want with the powerful and easy
to use Groovy programming language. Here is an example:
</p>

<pre><code>
jobDetail = new org.quartz.JobDetail('job1', 'group1', myschedule.job.sample.SimpleJob.class)
trigger = new org.quartz.CronTrigger('trigger1', 'group1', '0 * * * * ?')
quartzScheduler.scheduleJob(jobDetail, trigger)
</code></pre>
</p>

<p class="warning">
Warning: Groovy is a full featured programming language on top of Java. There is no 
restriction on what you can do with Groovy here. So do not do anything destructive such
as deleting files on system etc!
</p>

<div class="form">
<form method="post" action="${ mainPath }/scripting/run-action">
<textarea name="groovyScriptText" cols="78" rows="10">${ data.groovyScriptText }</textarea>
<br/>
<input type="submit" value="Run"></input>
</form>
</div>

<%@ include file="/WEB-INF/views/footer.inc" %>