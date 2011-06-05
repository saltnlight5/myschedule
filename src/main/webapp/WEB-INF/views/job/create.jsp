<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Creating New Job</h1>
<p id="info">
You need two items to create a job: JobDetail and Trigger. This form allow you to use 
<a href="http://groovy.codehaus.org">Groovy</a> script to
return these two objects, and it will be added to scheduler upon success submission.
</p>

<p id="warning">
Warning: Groovy is a full featured programming language on top of Java. There is no 
restriction on what you can do with Groovy here. So do not do anything destructive such
as deleting files on system!
</p>

<form method="post" action="${ actionPath }/job/create-process">

<p id="label">
Enter groovy script to return an <code>org.quartz.JobDetail</code> instance. 
<br>
Eg: <span class="plaintext">new org.quartz.JobDetail('job1', 'group1', myschedule.job.sample.SimpleJob.class)</span>
</p>
<textarea name="jobDetailScript" cols="78" rows="5">${ data.jobDetailScript }</textarea>
<br/>

<p id="label">
Enter groovy script to return an <code>org.quartz.Trigger</code> instance.
<br>
Eg: <span class="plaintext">new org.quartz.CronTrigger('trigger1', 'group1', "0 * * * * ?")</span>
</p>
<textarea name="triggerScript" cols="78" rows="5">${ data.triggerScript }</textarea>
<br/>

<input type="submit" value="Submit"></input>
</form>

<%@ include file="/WEB-INF/views/footer.inc" %>