<h2>Trigger : <%= request.getAttribute("trigger").getClass().getName() %> (Status: ${triggerStatus})</h2>
<table id="triggers-${loopIndex}">
	<thead>
	<tr>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<tr><td>Key</td><td>${ trigger.key }</td></tr>
	<tr><td>JobKey</td><td>${ trigger.jobKey }</td></tr>
	<tr><td>Description</td><td>${ trigger.description }</td></tr>
	<tr><td>Calendar Name</td><td>${ trigger.calendarName }</td></tr>
	<tr><td>Fire Instance Id</td><td>${ trigger.fireInstanceId }</td></tr>
	<tr><td>Misfire Instruction</td><td><%= myschedule.service.quartz.QuartzUtils.getMisfireInstructionName((org.quartz.Trigger)request.getAttribute("trigger")) %></td></tr>
	<tr><td>Priority</td><td>${ trigger.priority }</td></tr>
	<tr><td>Trigger Listeners</td><td>${ triggerListenerNames }</td></tr>
	
	<tr><td>Trigger Class</td><td><%= request.getAttribute("trigger").getClass().getName() %></td></tr>
	<tr><td>Next Fire Time</td><td>${ trigger.nextFireTime }</td></tr>
	<tr><td>Previous Fire Time</td><td>${ trigger.previousFireTime }</td></tr>
	<tr><td>Start Time</td><td>${ trigger.startTime }</td></tr>
	<tr><td>End Time</td><td>${ trigger.endTime }</td></tr>
	
	<% if (myschedule.service.quartz.QuartzUtils.isSimpleTrigger(request.getAttribute("trigger"))) { %>
	<tr><td>Final Fire Time</td><td>${ trigger.finalFireTime }</td></tr>
	<tr><td>Repeat Count</td><td>${ trigger.repeatCount }</td></tr>
	<tr><td>Repeat Interval</td><td>${ trigger.repeatInterval }</td></tr>
	<tr><td>Times Triggered</td><td>${ trigger.timesTriggered }</td></tr>
	
	<% } else if (myschedule.service.quartz.QuartzUtils.isCronTrigger(request.getAttribute("trigger"))) { %>
	<tr><td>Cron Expression</td><td>${ trigger.cronExpression }</td></tr>
	<tr><td>Expression Summary</td><td><pre>${ trigger.expressionSummary }</pre></td></tr>
	<tr><td>Time Zone</td><td>${ trigger.timeZone.displayName }</td></tr>
	<% } %>
	
	<c:forEach items="${ trigger.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>
	</tbody>
</table>
