<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Details on Job ${ data.jobDetail.fullName } - Trigger ${ data.trigger.fullName }</h1>

<h2>Job Detail</h2>
<table class="simple datalist">
	<tr><td>Name</td><td>${ data.jobDetail.name }</td></tr>
	<tr><td>Group</td><td>${ data.jobDetail.group }</td></tr>
	<tr><td>Job Class</td><td>${ data.jobDetail.jobClass }</td></tr>
	<tr><td>Key</td><td>${ data.jobDetail.key }</td></tr>
	<tr><td>Description</td><td>${ data.jobDetail.description }</td></tr>
	<tr><td>Durability</td><td>${ data.jobDetail.durable }</td></tr>
	<tr><td>Stateful</td><td>${ data.jobDetail.stateful }</td></tr>
	<tr><td>Volatile</td><td>${ data.jobDetail.volatile }</td></tr>
	<tr><td>Should Recover</td><td>${ data.jobDetailShouldRecover }</td></tr>
	<tr><td>Job Listeners</td><td>${ data.jobListenerNames }</td></tr>
	
	<c:forEach items="${ data.jobDetail.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>
	
</table>

<h2>Trigger : ${ data.trigger.class }</h2>
<table class="simple datalist">
	<tr><td>Name</td><td>${ data.trigger.name }</td></tr>
	<tr><td>Group</td><td>${ data.trigger.group }</td></tr>
	<tr><td>Key</td><td>${ data.trigger.key }</td></tr>
	<tr><td>Description</td><td>${ data.trigger.description }</td></tr>
	<tr><td>Calendar Name</td><td>${ data.trigger.calendarName }</td></tr>
	<tr><td>Fire Instance Id</td><td>${ data.trigger.fireInstanceId }</td></tr>
	<tr><td>Misfire Instruction</td><td>${ data.trigger.misfireInstruction }</td></tr>
	<tr><td>Priority</td><td>${ data.trigger.priority }</td></tr>
	<tr><td>Trigger Listeners</td><td>${ data.triggerListenerNames }</td></tr>
	
	<tr><td>Trigger Class</td><td>${ data.trigger.class }</td></tr>
	<tr><td>Next Fire Time</td><td>${ data.trigger.nextFireTime }</td></tr>
	<tr><td>Previous Fire Time</td><td>${ data.trigger.previousFireTime }</td></tr>
	<tr><td>Start Time</td><td>${ data.trigger.startTime }</td></tr>
	<tr><td>End Time</td><td>${ data.trigger.endTime }</td></tr>
	
	<c:choose>
	<c:when test="${ 'org.quartz.SimpleTrigger' == data.trigger.class.name }">
	<tr><td>Final Fire Time</td><td>${ data.trigger.finalFireTime }</td></tr>
	<tr><td>Repeat Count</td><td>${ data.trigger.repeatCount }</td></tr>
	<tr><td>Repeat Interval</td><td>${ data.trigger.repeatInterval }</td></tr>
	<tr><td>Times Triggered</td><td>${ data.trigger.timesTriggered }</td></tr>
	</c:when>
	
	<c:when test="${ 'org.quartz.CronTrigger' == data.trigger.class.name }">
	<tr><td>Cron Expression</td><td>${ data.trigger.cronExpression }</td></tr>
	<tr><td>Expression Summary</td><td>${ data.trigger.expressionSummary }</td></tr>
	<tr><td>Time Zone</td><td>${ data.trigger.timeZone.displayName }</td></tr>
	</c:when>
	
	</c:choose>
	
	<c:forEach items="${ data.trigger.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>

</table>

<h2>Trigger's Next ${ data.fireTimesCount } FireTimes</h2>
<table class="simple datalist">
	<c:forEach items="${ data.nextFireTimes }" var="time" varStatus="status">
	<tr>
		<td>${ status.index + 1 }</td>
		<td>${ time }</td>
	</tr>
	</c:forEach>
</table>

<%@ include file="/WEB-INF/views/footer.inc" %>