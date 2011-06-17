<h2>Trigger : ${ trigger.class }</h2>
<table class="outlined datalist">
	<tr><td>Name</td><td>${ trigger.name }</td></tr>
	<tr><td>Group</td><td>${ trigger.group }</td></tr>
	<tr><td>JobName</td><td>${ trigger.jobName }</td></tr>
	<tr><td>JobGroup</td><td>${ trigger.jobGroup }</td></tr>
	<tr><td>Description</td><td>${ trigger.description }</td></tr>
	<tr><td>Key</td><td>${ trigger.key }</td></tr>
	<tr><td>Calendar Name</td><td>${ trigger.calendarName }</td></tr>
	<tr><td>Fire Instance Id</td><td>${ trigger.fireInstanceId }</td></tr>
	<tr><td>Misfire Instruction</td><td>${ trigger.misfireInstruction }</td></tr>
	<tr><td>Priority</td><td>${ trigger.priority }</td></tr>
	<tr><td>Trigger Listeners</td><td>${ triggerListenerNames }</td></tr>
	
	<tr><td>Trigger Class</td><td>${ trigger.class }</td></tr>
	<tr><td>Next Fire Time</td><td>${ trigger.nextFireTime }</td></tr>
	<tr><td>Previous Fire Time</td><td>${ trigger.previousFireTime }</td></tr>
	<tr><td>Start Time</td><td>${ trigger.startTime }</td></tr>
	<tr><td>End Time</td><td>${ trigger.endTime }</td></tr>
	
	<c:choose>
	<c:when test="${ 'org.quartz.SimpleTrigger' == trigger.class.name }">
	<tr><td>Final Fire Time</td><td>${ trigger.finalFireTime }</td></tr>
	<tr><td>Repeat Count</td><td>${ trigger.repeatCount }</td></tr>
	<tr><td>Repeat Interval</td><td>${ trigger.repeatInterval }</td></tr>
	<tr><td>Times Triggered</td><td>${ trigger.timesTriggered }</td></tr>
	</c:when>
	
	<c:when test="${ 'org.quartz.CronTrigger' == trigger.class.name }">
	<tr><td>Cron Expression</td><td>${ trigger.cronExpression }</td></tr>
	<tr><td>Expression Summary</td><td>${ trigger.expressionSummary }</td></tr>
	<tr><td>Time Zone</td><td>${ trigger.timeZone.displayName }</td></tr>
	</c:when>
	
	</c:choose>
	
	<c:forEach items="${ trigger.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>

</table>
