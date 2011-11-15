<h2>Trigger : ${ triggerWrapper.className } (Status: ${ triggerWrapper.triggerStatusName })</h2>
<table class="trigger-table" id="trigger-index-${ triggerWrapper.listIndex }">
	<thead>
	<tr>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<tr><td>Key</td><td>${ triggerWrapper.trigger.key }</td></tr>
	<tr><td>JobKey</td><td>${ triggerWrapper.trigger.jobKey }</td></tr>
	<tr><td>Description</td><td>${ triggerWrapper.trigger.description }</td></tr>
	<tr><td>Calendar Name</td><td>${ triggerWrapper.trigger.calendarName }</td></tr>
	<tr><td>Fire Instance Id</td><td>${ triggerWrapper.trigger.fireInstanceId }</td></tr>
	<tr><td>Misfire Instruction</td><td>${ triggerWrapper.misfireInstructionName }</td></tr>
	<tr><td>Priority</td><td>${ triggerWrapper.trigger.priority }</td></tr>
	
	<tr><td>Trigger Class</td><td>${ triggerWrapper.className }</td></tr>
	<tr><td>Next Fire Time</td><td>${ triggerWrapper.trigger.nextFireTime }</td></tr>
	<tr><td>Previous Fire Time</td><td>${ triggerWrapper.trigger.previousFireTime }</td></tr>
	<tr><td>Start Time</td><td>${ triggerWrapper.trigger.startTime }</td></tr>
	<tr><td>End Time</td><td>${ triggerWrapper.trigger.endTime }</td></tr>
	
	<c:choose>
	
	<c:when test="${ triggerWrapper.isSimpleTrigger }">
	<tr><td>Repeat Count</td><td>${ triggerWrapper.trigger.repeatCount }</td></tr>
	<tr><td>Repeat Interval</td><td>${ triggerWrapper.trigger.repeatInterval }</td></tr>
	</c:when>
	
	<c:when test="${ triggerWrapper.isCronTrigger }">
	<tr><td>Cron Expression</td><td>${ triggerWrapper.trigger.cronExpression }</td></tr>
	<tr><td>Expression Summary</td><td><pre>${ triggerWrapper.trigger.expressionSummary }</pre></td></tr>
	<tr><td>Time Zone</td><td>${ triggerWrapper.trigger.timeZone.displayName }</td></tr>
	</c:when>
	
	<c:when test="${ triggerWrapper.isCalendarIntervalTrigger }">
	<tr><td>Repeat Interval</td><td>${ triggerWrapper.trigger.repeatInterval }</td></tr>
	<tr><td>Repeat Interval Unit</td><td>${ triggerWrapper.trigger.repeatIntervalUnit }</td></tr>tr>
	</c:when>
	
	<c:when test="${ triggerWrapper.isDailyTimeIntervalTrigger }">
	<tr><td>Start Time of Day</td><td>${ triggerWrapper.trigger.startTimeOfDay }</td></tr>
	<tr><td>End Time of Day</td><td>${ triggerWrapper.trigger.endTimeOfDay }</td></tr>
	<tr><td>Days of Week</td><td>${ triggerWrapper.trigger.daysOfWeek }</td></tr>
	<tr><td>Repeat Interval</td><td>${ triggerWrapper.trigger.repeatInterval }</td></tr>
	<tr><td>Repeat Interval Unit</td><td>${ triggerWrapper.trigger.repeatIntervalUnit }</td></tr>
	<tr><td>Repeat Count</td><td>${ triggerWrapper.trigger.repeatCount }</td></tr>
	</c:when>
	
	</c:choose>
	
	<c:forEach items="${ triggerWrapper.trigger.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>
	
	</tbody>
</table>
