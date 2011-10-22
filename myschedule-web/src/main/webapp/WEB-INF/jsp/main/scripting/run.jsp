<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
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

<p>This is for the power user who want to manipulate the scheduler with a ScriptingEngine.</p>

<a id="show-help" href="#">Show an example</a>
<div id="help">
<p>In your script, these variables are available to use immediately.</p>
<pre>
scheduler           An instance of myschedule.quartz.extra.SchedulerTemplate that wraps org.quartz.Scheuler API, and it
                    provides many additional convenient methods for scheduling jobs.
webOut              An instance of java.io.PrintWriter to allow script to display text output back to web page for debug purpose.
</pre>

<p>For example, using Groovy, here is how you add a new job to the scheduler that runs every MON-FRI at 8am.</p>
<pre>
import myschedule.quartz.extra.job.*
dataMap = [
  'ScriptEngineName': 'Groovy',
  'ScriptText': '''
    logger.info("It will take me a min to wake up.")
    sleep(60000)
  '''
]
nextFireTime = scheduler.scheduleCronJob('MyDailyJob', '0 0 8 ? * MON-FRI', ScriptingJob.class, dataMap)
webOut.println('MyDailyJob has been scheduled. Next fire time: ' + nextFireTime)
</pre>

You can find more <a href="http://code.google.com/p/myschedule/wiki/ScriptingScheduler">examples here.</a>

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
<textarea  style="width: 100%; height: 15em;" name="scriptText">${ data.groovyScriptText }</textarea>
<br/>
Scripting Language: <select name="scriptEngineName">
<c:forEach items="${ data.scriptEngineNames }" var="name">
<option value="${ name }">${ name }</option>
</c:forEach>
</select>
<br/>
<input type="submit" value="Run"></input>
</form>

</div><!-- page-container -->

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>