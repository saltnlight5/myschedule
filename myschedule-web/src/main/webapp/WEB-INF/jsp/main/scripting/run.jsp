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
	$("#variables").hide();
	$('#variables-link').click(function() {
		$("#variables").toggle('slow');
	});
	
	// Click examles to load textarea.
	$("#script-simpleJobs").click(function() {
		var scriptEngineName =  $("#scriptEngineName option:selected").val();
		$("#scriptText").load("${ mainPath }/scripting/get-script-eg?name=simpleJobs&scriptEngineName=" + scriptEngineName);
	});
	$("#script-cronJobs").click(function() {
		var scriptEngineName =  $("#scriptEngineName option:selected").val();
		$("#scriptText").load("${ mainPath }/scripting/get-script-eg?name=cronJobs&scriptEngineName=" + scriptEngineName);
	});
});
</script>

<div id="page-container">
<h1>Scheduler Scripting</h1>

<p>Run any script using Java ScriptEngine to manage the scheduler. You will have these 
<a id="variables-link" href="#">implicit variables </a> available. </p>
<div id="variables">
<pre>
scheduler - An instance of myschedule.quartz.extra.SchedulerTemplate that wraps org.quartz.Scheuler API, and it
            provides many additional convenient methods for scheduling jobs.
webOut    - An instance of java.io.PrintWriter to allow script to display text output back to web page for debug purpose.
</pre>
</div><!-- div.variables -->

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

<label class="label">
Script Examples:
<span style="label-notes"> 
	<a name="script-simpleJobs" id="script-simpleJobs">Fixed Interval Jobs</a>
	, <a name="script-cronJobs" id="script-cronJobs">Cron Jobs</a>
	, <a href="http://code.google.com/p/myschedule/wiki/ScriptingScheduler">More ...</a>
</span>
</label>

<textarea  style="width: 100%; height: 15em;" id="scriptText" name="scriptText">${ data.scriptText }</textarea>

<br/>
Scripting Language: 
<select id="scriptEngineName" name="scriptEngineName">
<c:forEach items="${ data.scriptEngineNames }" var="name">
<c:set var="selectedOpt" value=""/>
<c:if test="${ name ==  selectedScriptEngineName }">
	<c:set var="selectedOpt" value="selected=\"true\""/>
</c:if>
<option ${ selectedOpt } value="${ name }">${ name }</option>
</c:forEach>
</select>
<br/>
<input type="submit" value="Run"></input>
</form>

</div><!-- page-container -->

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>