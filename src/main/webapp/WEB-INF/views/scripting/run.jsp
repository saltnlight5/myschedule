<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Creating New Job</h1>
<p id="info">
You need two items to create a job: JobDetail and Trigger. This form allow you to use 
<a href="http://groovy.codehaus.org">Groovy</a> script to
return these two objects, and it will be added to scheduler upon success submission. You
may also have more than one triggers in the end of the list, and they all will be add for 
the same job.
</p>

<p id="info">
Enter groovy script to return an list one <code>org.quartz.JobDetail</code> and one or 
more <code>org.quartz.Trigger</code> instances. Example:
</p>

<pre><code>
jobDetail = new org.quartz.JobDetail('job1', 'group1', myschedule.job.sample.SimpleJob.class)
trigger = new org.quartz.CronTrigger('trigger1', 'group1', '0 * * * * ?')
[jobDetail, trigger]
</code></pre>
</p>

<p id="warning">
Warning: Groovy is a full featured programming language on top of Java. There is no 
restriction on what you can do with Groovy here. So do not do anything destructive such
as deleting files on system!
</p>

<form method="post" action="${ actionPath }/job/create-action">
<textarea name="groovyScriptText" cols="78" rows="10">${ data.groovyScriptText }</textarea>
<br/>
<input type="submit" value="Submit"></input>
</form>

<%@ include file="/WEB-INF/views/footer.inc" %>