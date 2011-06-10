<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<script>
$(document).ready(function() {
	// Hide the help page on startup.
	$("#help").hide();
	
	// Reponse to show page link
	$('#showhelp').click(function() {
		$("#help").toggle('slow');
	});
});
</script>

<div class="content">

<h1>Scheduler Scripting</h1>

<div class="info">This is for the power user who want to manipulate the scheduler with 
<a href="http://groovy.codehaus.org">Groovy</a> scripting.</div>

<a id="showhelp" href="#">Show more help and example</a>

<div id="help" class="help">
In your script, these variables are available to use immediately:
<pre>
	webOut - An instance of java.io.PrintWriter to allow script to display output to web page after Run.
	quartzScheduler - An instance of org.quartz.Scheduler scheduler in this application.
</pre>

For example, here is how you schedule a new job to the scheduler that runs every minute:
<pre>
	import org.quartz.*
	name = 'GroovyJob' + System.currentTimeMillis()
	jobDetail = new JobDetail(name, 'DEFAULT', myschedule.job.sample.SimpleJob.class)
	trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
	quartzScheduler.scheduleJob(jobDetail, trigger)
	webOut.println('Job scheduled successfully: ' + jobDetail.fullName)
</pre>

<div class="warning">
Warning: Groovy is a full featured programming language on top of Java. There is no 
restriction on what you can do with Groovy here. So do not do anything destructive such
as deleting files on system etc!
</div>
</div>

<div class="form">
<form method="post" action="${ mainPath }/scripting/run-action">
<textarea name="groovyScriptText" cols="78" rows="10">${ data.groovyScriptText }</textarea>
<br/>
<input type="submit" value="Run"></input>
</form>
</div>

</div> <!--  div.content -->

<%@ include file="/WEB-INF/views/footer.inc" %>