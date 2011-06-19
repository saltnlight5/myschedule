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

<div class="fixedwidthpane centerpane">

<div class="padvertext">This is for the power user who want to manipulate the scheduler with 
<a href="http://groovy.codehaus.org">Groovy</a> scripting.</div>

<div class="padvertext"><a id="showhelp" href="#">Show an example</a></div>

<div id="help" class="padvertext">
In your script, these variables are available to use immediately:
<pre>
webOut - An instance of java.io.PrintWriter to allow script to display output to web page after Run.
quartzScheduler - An instance of org.quartz.Scheduler scheduler in this application.
</pre>

For example, here is how you schedule three new jobs to the scheduler that each runs every minute:
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
<textarea  class="fixedwidthpane" style="height: 300px;" name="groovyScriptText">${ data.groovyScriptText }</textarea>
<br/>
<input type="submit" value="Run"></input>
</form>

</div><!-- div.fixedwidthpane -->

</div><!-- div.content -->
<%@ include file="/WEB-INF/views/footer.inc" %>