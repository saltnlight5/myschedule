<%@ include file="/WEB-INF/views/page-a.inc" %>
<%@ include file="/WEB-INF/views/menu.inc" %>
<style>
#myschedule textarea {
	width: 100%;
	height: 200px;
}
</style>
<script>
$(document).ready(function() {
	// Hide the help page on startup.
	$("#help").hide();
	
	// Reponse to show page link
	$('#show-help').click(function() {
		$("#help").toggle('slow');
	});
});
</script>

<div id="page-container">
<h1>Scheduler Scripting</h1>

<p>This is for the power user who want to manipulate the scheduler with 
<a href="http://groovy.codehaus.org">Groovy</a> scripting.</p>

<a id="show-help" href="#">Show an example</a>
<div id="help">
<p>In your script, these variables are available to use immediately.</p>
<pre>
webOut - An instance of java.io.PrintWriter to allow script to display output to web page after Run.
quartzScheduler - An instance of org.quartz.Scheduler scheduler in this application.
</pre>

<p>For example, here is how you schedule three new jobs to the scheduler that each runs every minute.</p>
<pre>
import org.quartz.*
3.times { i ->
  name = 'GroovyJob' + i
  jobDetail = new JobDetail(name, 'DEFAULT', myschedule.job.sample.SimpleJob.class)
  trigger = new CronTrigger(name, 'DEFAULT', '0 * * * * ?')
  quartzScheduler.scheduleJob(jobDetail, trigger)
  webOut.println('Job scheduled successfully: ' + jobDetail.fullName)
}
</pre>

<div class="warning">
Warning: Groovy is a full featured programming language on top of Java. There is no 
restriction on what you can do with Groovy here. So do not do anything destructive such
as deleting files on system etc!
</div>

</div><!-- div.help -->

<form method="post" action="${ mainPath }/scripting/run-action">
<textarea  style="width: 100%; height: 15em;" name="groovyScriptText">${ data.groovyScriptText }</textarea>
<br/>
<input type="submit" value="Run"></input>
</form>

</div><!-- page-container -->

<%@ include file="/WEB-INF/views/page-b.inc" %>