<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>
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
<h1>Load Job Scheduling Data</h1>

<p>Paste your xml content in the form and submit.</p>

<a id="show-help" href="#">Show an example</a>

<div id="help">
<p>Here is an simple example of scheduling new job with xml.</p>
<pre style='color:#000000;background:#ffffff;'><span style='color:#004a43; '>&lt;?</span><span style='color:#004a43; '>xml</span> <span style='color:#004a43; '>version</span><span style='color:#808030; '>=</span><span style='color:#008c00; '>'1.0'</span> <span style='color:#004a43; '>encoding</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>'utf-8'</span><span style='color:#004a43; '>?></span>
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
<p>There is an error when loading xml data: <pre>${ data.errorMessage }</pre></p>
<a id="show-exception" href="#">Show exception stacktrace</a>
<div id="exception"><pre>${ data.fullStackTrace }</pre></div>
</div>
</c:if>

<form method="post" action="${ mainPath }/job/load-xml-action">
<textarea style="width: 100%; height: 15em;" name="xml">${ data.xml }</textarea>
<br/>
<input type="submit" value="Load"></input>
</form>

</div> <!-- page-container -->
<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>