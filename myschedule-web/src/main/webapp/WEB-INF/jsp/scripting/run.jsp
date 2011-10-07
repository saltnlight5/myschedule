<%@ include file="/WEB-INF/jsp/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/menu.inc" %>
<style>
#myschedule textarea {
	width: 100%;
	height: 200px;
}
</style>
<script>
$(document).ready(function() {
	// Auto hide help page, and show when click.
	$("#help").hide();
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
scheduler           An instance of myschedule.quartz.extra.SchedulerTemplate that wraps org.quartz.Scheuler API, and it
                    provides many additional convenient methods for scheduling jobs.
webOut              An instance of java.io.PrintWriter to allow script to display text output back to web page for debug purpose.
</pre>

<p>For example, here is how you schedule a new job to the scheduler that runs every MON-FRI at 8am.</p>
<pre>
import myschedule.quartz.extra.job.*
job = scheduler.createJobDetail('MyDailyJob', ScriptingJob.class)
map = job.getJobDataMap()
map.put('ScriptEngineName', 'Groovy')
map.put('ScriptText', 
'''
  logger.info("It will take me a min to wake up.")
  sleep(60000)
''')
trigger = scheduler.createCronTrigger('MyDailyJob', '0 0 8 ? * MON-FRI')
nextFireTime = scheduler.scheduleJob(job, trigger)
webOut.println('MyDailyJob has been scheduled. Next fire time: ' + nextFireTime)
</pre>

You can find more <a href="http://code.google.com/p/myschedule/wiki/ScriptingScheduler">examples here.</a>

<div class="warning">
Warning: Groovy is a full featured programming language on top of Java. There is no 
restriction on what you can do with Groovy here. So do not do anything destructive such
as deleting files on system etc!
</div>

</div><!-- div.help -->

<c:if test="${ not empty data.fullStackTrace }">
<script>
$(document).ready(function() {
	// Auto hide help page, and show when click.
	$("#exception").hide();
	$('#show-exception').click(function() {
		$("#exception").toggle('slow');
	});
});
</script>
<div class="error">
<p>There is an error when evaluating your script: <pre>${ data.errorMessage }</pre></p>
<a id="show-exception" href="#">Show exception stacktrace</a>
<div id="exception"><pre>${ data.fullStackTrace }</pre></div>
</div>
</c:if>

<form method="post" action="${ mainPath }/scripting/run-action">
<textarea  style="width: 100%; height: 15em;" name="groovyScriptText">${ data.groovyScriptText }</textarea>
<br/>
<input type="submit" value="Run"></input>
</form>

</div><!-- page-container -->

<%@ include file="/WEB-INF/jsp/page-b.inc" %>