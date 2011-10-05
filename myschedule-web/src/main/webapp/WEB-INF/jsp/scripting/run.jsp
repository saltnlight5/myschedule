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
webOut              An instance of java.io.PrintWriter to allow script to display output to web page after Run.
schedulerTemplate   An instance of myschedule.service.quartz.SchedulerTemplate that provide simplified 
                    template to manage a scheduler implemenation (such as the Quartz's Scheduler).
quartzScheduler     An instance of org.quartz.Scheduler scheduler in this application.
</pre>

<p>For example, here is how you schedule three new jobs to the scheduler that each runs every minute.</p>
<pre>
name = 'MintelyJob'
repeatForever = -1
repeatMillisInterval = 60000
jobClass = myschedule.job.sample.SimpleJob.class
schedulerTemplate.scheduleRepeatableJob(name, repeatForever, repeatMillisInterval, jobClass)
webOut.println('Job scheduled successfully: ' + name)
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