<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/dashboard/menu.inc" %>

<h1>About MySchedule</h1>

<p>
The MySchedule is a web application UI for managing <a href="http://quartz-scheduler.org">Quartz Scheduler</a>.</p>

<p>
The Quartz Scheduler is a Java system that supports flexible scheduling such as fixed repeating interval or CRON jobs. 
Developers may write custom job task in Java and use Quartz triggers to schedule the job. Quartz also has optional 
database level data storage, and it allow fine control on job executions with thread pool and clustering capability.</p>

<p>Although Quartz is a powerful library, but it's target users are developers. You can not get a job scheduled without 
writing some Java code. The MySchedule project here provide not only some extra quartz components for easy programming, 
but we also provide an end-user friendly way to access and manage the Quartz scheduler. You can get it up and running 
without little or no programming necessary. It will host and run the full scheduler inside the servlet application.</p>

<p>MySchedule is an Open Source software and licensed with Apache License 2.0. Please visit the 
<a href="http://code.google.com/p/myschedule">project site</a> for more information.</p>

<p>This web application is brought to you by <a href="mailto:saltnlight5@gmail.com">Zemian Deng</a>, who is passionate 
about software development. If you find it useful, please send him a note and tell him how you're using
it.</p>

<p>This web application is powered by the following technologies and libraries.</p>
<div id="poweredBy">
	<a href="http://www.oracle.com/java">Java/Servlet</a>
	<a href="http://www.quartz-shecheduler.org">Quartz</a>
	<a href="http://www.jquery.org">jQuery/UI</a>
</div>

<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>