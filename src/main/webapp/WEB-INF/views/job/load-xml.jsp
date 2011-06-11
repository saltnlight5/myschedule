<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

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

<h1>Load Job Scheduling Data</h1>

<div class="fixedwidthpane centerpane">

<div class="padvertext">Paste your xml content in the form and submit.</div>

<div class="padvertext"><a id="showhelp" href="#">Show an example</a></div>

<div id="help" class="padvertext">
Here is an simple example of scheduling new job with xml:
<pre style='color:#000000;background:#ffffff;'><span style='color:#004a43; '>&lt;?</span><span style='color:#004a43; '>xml</span> <span style='color:#004a43; '>version</span><span style='color:#808030; '>=</span><span style='color:#008c00; '>'1.0'</span> <span style='color:#004a43; '>encoding</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>'utf-8'</span><span style='color:#004a43; '>?></span>
<span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job-scheduling-data</span> <span style='color:#274796; '>version</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#0000e6; '>1.8</span><span style='color:#0000e6; '>"</span> 
    <span style='color:#666616; '>xmlns</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.quartz-scheduler.org</span><span style='color:#40015a; '>/xml/JobSchedulingData</span><span style='color:#0000e6; '>"</span> 
    <span style='color:#666616; '>xmlns</span><span style='color:#800080; '>:</span><span style='color:#074726; '>xsi</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.w3.org</span><span style='color:#40015a; '>/2001/XMLSchema-instance</span><span style='color:#0000e6; '>"</span>
    <span style='color:#666616; '>xsi</span><span style='color:#800080; '>:</span><span style='color:#074726; '>schemaLocation</span><span style='color:#808030; '>=</span><span style='color:#0000e6; '>"</span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.quartz-scheduler.org</span><span style='color:#40015a; '>/xml/JobSchedulingData</span><span style='color:#0000e6; '> </span><span style='color:#666616; '>http</span><span style='color:#800080; '>:</span><span style='color:#800000; font-weight:bold; '>//</span><span style='color:#5555dd; '>www.quartz-scheduler.org</span><span style='color:#40015a; '>/xml/job_scheduling_data_1_8.xsd</span><span style='color:#0000e6; '>"</span><span style='color:#a65700; '>></span>

    <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>schedule</span><span style='color:#a65700; '>></span>    
        <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job</span><span style='color:#a65700; '>></span>
            <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>name</span><span style='color:#a65700; '>></span>minute_job<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>name</span><span style='color:#a65700; '>></span>
            <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>group</span><span style='color:#a65700; '>></span>DEFAULT<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>group</span><span style='color:#a65700; '>></span>
            <span style='color:#a65700; '>&lt;</span><span style='color:#5f5035; '>job-class</span><span style='color:#a65700; '>></span>myschedule.job.sample.SimpleJob<span style='color:#a65700; '>&lt;/</span><span style='color:#5f5035; '>job-class</span><span style='color:#a65700; '>></span>
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
</div>

<form method="post" action="${ mainPath }/job/load-xml-action">
<textarea class="fixedwidthpane" style="height: 300px;" name="xml">${ data.xml }</textarea>
<br/>
<input type="submit" value="Load"></input>
</form>

</div><!-- div.fixedwithpane -->

</div><!-- div.content -->
<%@ include file="/WEB-INF/views/footer.inc" %>