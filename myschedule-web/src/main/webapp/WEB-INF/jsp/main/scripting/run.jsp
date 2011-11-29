<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>

<style>
#run-script-form { width: 100%; }
#run-script-form textarea { 
	font-size: 15px;
	font-family: monospace;
	height: 400px;
	font-height: 17px;
	margin-top: 2px;
	margin-bottom: 2px;
	width: 99.5%;
}
</style>
<script>
$(document).ready(function() {
	$('#tabs').tabs({
	    select: function(event, ui) {
	        var url = $.data(ui.tab, 'load.tabs');
	        if( url ) {
	            location.href = url;
	            return false;
	        }
	        return true;
	    }
	});	
	

	// Auto hide help page, and show when click.
	$("#variables").hide();
	$('#variables-link').click(function() {
		$("#variables").toggle('slow');
	});
	
	// Click examles to load textarea.
	$("#script-simpleJobs").click(function() {
		var scriptEngineName =  $("#scriptEngineName option:selected").val();
		$("#scriptText").load("${ mainPath }/scripting/run-script-sample?name=simpleJobs&scriptEngineName=" + scriptEngineName);
	});
	$("#script-cronJobs").click(function() {
		var scriptEngineName =  $("#scriptEngineName option:selected").val();
		$("#scriptText").load("${ mainPath }/scripting/run-script-sample?name=cronJobs&scriptEngineName=" + scriptEngineName);
	});
	$("#script-advanceJobs").click(function() {
		var scriptEngineName =  $("#scriptEngineName option:selected").val();
		$("#scriptText").load("${ mainPath }/scripting/run-script-sample?name=advanceJobs&scriptEngineName=" + scriptEngineName);
	});
	$("#script-calendarJobs").click(function() {
		var scriptEngineName =  $("#scriptEngineName option:selected").val();
		$("#scriptText").load("${ mainPath }/scripting/run-script-sample?name=calendarJobs&scriptEngineName=" + scriptEngineName);
	});
});
</script>

<div id="tabs">
	<ul>
	<li><a href="#">Scripting</a></li>
	</ul>
	
	<div id="tabs-1">
	
		<div id="page-container">
		<h1>Scheduler Scripting</h1>
		
		<p>Run any script using Java ScriptEngine to manage the scheduler. You will have these 
		<a id="variables-link" href="#">implicit variables </a> available. </p>
		<div id="variables">
		<pre>
		scheduler - An instance of myschedule.quartz.extra.SchedulerTemplate that allow schedule and manage jobs.
		output    - An instance of java.io.PrintWriter that allow script to display text back to web page.
		logger    - An instance of org.slf4j.Logger that allow script to log messages in server log.
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
		<p>There is an error when evaluating your script: 
		<pre>${ data.errorMessage }</pre></p>
		<a id="show-exception" href="#">Show full stacktrace exception</a>
		<div id="exception"><pre>${ data.fullStackTrace }</pre></div>
		</div>
		</c:if>
		
		<form id="run-script-form" method="post" action="${ mainPath }/scripting/run-action">		
			<label class="label">
			Script Examples:
			<span style="label-notes"> 
				<a name="script-simpleJobs" id="script-simpleJobs">Fixed Interval Jobs</a>
				, <a name="script-cronJobs" id="script-cronJobs">Cron Jobs</a>
				, <a name="script-advanceJobs" id="script-advanceJobs">Advance Jobs</a>
				, <a name="script-calendarJobs" id="script-calendarJobs">Quartz Calendar Jobs</a>
				, <a href="http://code.google.com/p/myschedule/wiki/ScriptingScheduler">More ...</a>
			</span>
			</label>
			
			<textarea id="scriptText" name="scriptText">${ data.scriptText }</textarea>
			
			<br/>
			Scripting Language: 
			<select id="scriptEngineName" name="scriptEngineName">
			<c:forEach items="${ data.scriptEngineNames }" var="name">
			<c:set var="selectedOpt" value=""/>
			<c:if test="${ name ==  data.selectedScriptEngineName }">
				<c:set var="selectedOpt" value="selected=\"true\""/>
			</c:if>
			<option ${ selectedOpt } value="${ name }">${ name }</option>
			</c:forEach>
			</select>
			
			<br/>
			<input type="submit" value="Run"></input>
		</form>
		
		</div><!-- page-container -->
		
	</div>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>