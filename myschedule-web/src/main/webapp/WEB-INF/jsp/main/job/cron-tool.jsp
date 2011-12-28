<%@ include file="/WEB-INF/jsp/main/page-a.inc" %>
<%@ include file="/WEB-INF/jsp/main/menu.inc" %>
<%@ include file="/WEB-INF/jsp/main/job/submenu.inc" %>

<h1>Cron Expression Validation Tool</h1>

<c:if test="${ data.invalidCron }">
	<div class="error">
	<p>The cron expression entered is invalid. ${ data.exceptionStr }</p>
	</div>
</c:if>
<c:if test="${ data.invalidAfterTime }">
	<div class="error">
	<p>The future fire time starting date entry is invalid. Please enter date in 'MM/DD/YYYY HH:MM:SS' format.</p>
	</div>
</c:if>

<form action="" method="post">
	<input type="hidden" name="validate" value="true"/>
	<label>Cron Expression (<a href="http://quartz-scheduler.org/api/2.1.0/org/quartz/CronExpression.html">help</a>)
	</label> <input name="cron" type="text" value="${ data.cron }" size="50"/><br/>
	<label>Num of future fire time to display</label> <input name="fireTimesCount" type="text" value="${ data.fireTimesCount }"/><br/>
	<label>Starting future fire time</label> <input name="afterTime" type="text" value="${ data.afterTimeStr }"/><br/>
	<input type="submit" value="Validate Cron"></input>
</form>

<c:if test="${ not empty data.fireTimes }">
	<h2>Cron Expression is VALID! Here are some calculated future fire times</h2>
	<table>
		<tr>
			<td>INDEX</td>
			<td>FIRE TIME</td>
		</tr>
		<c:forEach items="${ data.fireTimes }" var="fireTime" varStatus="loop">
		<tr>
			<td>${ loop.index + 1}</td>
			<td>${ fireTime }</td>
		</tr>
		</c:forEach>
	</table>
</c:if>


<%@ include file="/WEB-INF/jsp/main/page-b.inc" %>