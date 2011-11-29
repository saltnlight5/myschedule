<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>

<style>
#xml-jobs-form { width: 100%; }
#xml-jobs-form textarea { 
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
	
	// Hide the help page on startup.
	$("#help").hide();
	
	// Reponse to show page link
	$('#show-help').click(function() {
		$("#help").toggle('slow');
	});

	// Exception error page.
	$("#exception").hide();
	$('#show-exception').click(function() {
		$("#exception").toggle('slow');
	});
});
</script>

<div id="tabs">
	<ul>
	<li><a href="#">Load Xml Jobs</a></li>
	</ul>
	
	<div id="tabs-1">
	
	<h1>Load Job Scheduling Data</h1>
	
	<p>Paste your xml content in the form and submit.</p>
	
	<a id="show-help" href="#">Show an example</a>
	
	<div id="help">
	<p>Here is an simple example of scheduling new job with xml.</p>
	<pre style='color:#000000;background:#ffffff;font-size: 12px;'><span style='color:#004a43; '>&lt;?</span><span style='color:#004a43; '>xml</span> <span style='color:#004a43; '>version</span><span style='color:#808030; '>=</span><span style='color:#008c00; '>'1.0'</span> <span style='color:#004a43; '>encoding</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>'utf-8'</span><span style='color:#004a43; '>?></span>
<span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job-scheduling-data</span> <span style='color:#274796; '>version</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#0000e6; '>2.0</span><span style='color:#0000e6; '>"</span> 
   <span style='color:#666616; '>xmlns</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.quartz-scheduler.org</span><span style='color:#40015a; '>/xml/JobSchedulingData</span><span style='color:#0000e6; '>"</span> 
   <span style='color:#666616; '>xmlns</span><span style='color:#800080; '>:</span><span style='color:#074726; '>xsi</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.w3.org</span><span style='color:#40015a; '>/2001/XMLSchema-instance</span><span style='color:#0000e6; '>"</span>
   <span style='color:#666616; '>xsi</span><span style='color:#800080; '>:</span><span style='color:#074726; '>schemaLocation</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.quartz-scheduler.org</span><span style='color:#40015a; '>/xml/JobSchedulingData</span><span style='color:#0000e6; '> </span>
   <span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.quartz-scheduler.org</span><span style='color:#40015a; '>/xml/job_scheduling_data_2_0.xsd</span><span style='color:#0000e6; '>"</span><span style='color:#a65700; '>></span>
   <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>schedule</span><span style='color:#a65700; '>></span>
       <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job</span><span style='color:#a65700; '>></span>
           <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>name</span><span style='color:#a65700; '>></span>minute_job<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>name</span><span style='color:#a65700; '>></span>
           <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>group</span><span style='color:#a65700; '>></span>DEFAULT<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>group</span><span style='color:#a65700; '>></span>
           <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job-class</span><span style='color:#a65700; '>></span>myschedule.quartz.extra.job.LoggerJob<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>job-class</span><span style='color:#a65700; '>></span>
       <span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>job</span><span style='color:#a65700; '>></span>        
       <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>trigger</span><span style='color:#a65700; '>></span>
           <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>simple</span><span style='color:#a65700; '>></span>
               <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>name</span><span style='color:#a65700; '>></span>minute_job<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>name</span><span style='color:#a65700; '>></span>
               <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>group</span><span style='color:#a65700; '>></span>DEFAULT<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>group</span><span style='color:#a65700; '>></span>
               <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job-name</span><span style='color:#a65700; '>></span>minute_job<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>job-name</span><span style='color:#a65700; '>></span>
               <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job-group</span><span style='color:#a65700; '>></span>DEFAULT<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>job-group</span><span style='color:#a65700; '>></span>
               <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>repeat-count</span><span style='color:#a65700; '>></span>-1<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>repeat-count</span><span style='color:#a65700; '>></span>
               <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>repeat-interval</span><span style='color:#a65700; '>></span>60000<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>repeat-interval</span><span style='color:#a65700; '>></span>
           <span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>simple</span><span style='color:#a65700; '>></span>
       <span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>trigger</span><span style='color:#a65700; '>></span>        
   <span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>schedule</span><span style='color:#a65700; '>></span>
<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>job-scheduling-data</span><span style='color:#a65700; '>></span>
	</pre>
	</div> <!-- div.help -->
	
	<c:if test="${ not empty data.fullStackTrace }">
		<div class="error">
			<p>There is an error when loading xml data: <pre>${ data.errorMessage }</pre></p>
			<a id="show-exception" href="#">Show exception stacktrace</a>
			<div id="exception"><pre>${ data.fullStackTrace }</pre></div>
		</div>
	</c:if>
	
	<form id="xml-jobs-form" method="post" action="${ mainPath }/job/load-xml-action">
		<textarea name="xml">${ data.xml }</textarea>
		<input type="submit" value="Add Jobs"></input>
	</form>
	
	</div>
</div>
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>