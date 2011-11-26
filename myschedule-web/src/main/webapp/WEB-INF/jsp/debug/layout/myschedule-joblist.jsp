<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<script src="${ contextPath }/js/jquery-1.7.1.min.js"></script>
	<script src="${ contextPath }/js/jquery-ui-1.8.13.custom.js"></script>
	<link rel="stylesheet" type="text/css" href="${ contextPath }/themes/${ themeName }/jquery-ui.custom.css" />
	<script>
$(document).ready(function() {
	$("#menu").buttonset();
	$('#tabs').tabs({
	    select: function(event, ui) {
	        var url = $.data(ui.tab, 'load.tabs');
	        if( url ) {
	            location.href = url;
	            return false;
	        }
	        return true;
	    }
	});
});
	</script>
	<style>	
#topline { background-color: black; width: 100%; height: 5px; }

#header { padding: 10px 10px 55px 10px; background-color: #333; }
#breadcrumbs-container { margin-bottom: 10px; }
#breadcrumbs { height: 40px; font-size: 20px; display:table-cell; vertical-align:middle; padding: 0px 10px 0px 10px; }
#breadcrumbs li { display: inline; list-style-type: none; padding-right: 5px; }
#menu-container { float: right; }
#content { padding: 10px; background-color: #fff; height: 600px; }

#footer { padding-top: 10px; text-align: center; color: #ccc; }
#footer a, #footer a:link, #footer a:visited { color: #ccc; text-decoration: none; }
	</style>
	<title>Debug</title>
</head>
<body class="ui-helper-reset" style="margin: 0px; padding: 0px; background-color: #666;">
<div id="topline"></div>
<div id="header">
	<div id="breadcrumbs-container" class="ui-state-default ui-corner-all">
		<ul id="breadcrumbs">
		<li><a class="breadcrumb_e breadcrumb_icon_home" href="${ debugServletPath }">Home</a> &#187 </li>
		<li><a href="${ debugServletPath }/layout/myschedule-joblist?m=1">Dashboard</a> &#187 </li>
		<li><a href="${ debugServletPath }/layout/myschedule-joblist?m=2">InMemoryQuartScheduler_$_NON_CLUSTER</a></li>
		</ul>
	</div>
	
	<div id="menu-container">
	<div id="menu">
		<a href="${ mainPath }/job/list">Scheduler Jobs</a>
		<a href="${ mainPath }/job/load-xml">Add Jobs</a>
		<a href="${ mainPath }/scheduler/index">Settings</a>
		<a href="${ mainPath }/scripting/run">Tools</a>
	</div>
	</div>
</div>

<div id="content">	 
	<div id="tabs">
		<ul>
		<li><a href="#tabs-1">Jobs with trigger</a></li>
		<li><a href="${ debugActionPath }/tab-sample?p=2">Jobs without trigger</a></li>
		<li><a href="${ debugActionPath }/tab-sample?p=3">Calendars</a></li>
		<li><a href="${ debugActionPath }/tab-sample?p=4">Currently Executing Jobs</a></li>
		<li><a href="${ debugActionPath }/tab-sample?p=5">Load Jobs (Xml)</a></li>
		</ul>
		
		<div id="tabs-1">
		
<table id="jobs" class="display">

	<thead>
	<tr>
		<th> JOB </th>
		<th> TRIGGER </th>
		<th> SCHEDULE </th>
		<th> NEXT FIRE TIME </th>

		<th> ACTIONS </th>
	</tr>
	</thead>
	
	<tbody>
	
	
	<tr>
		<td><a href="/main/job/job-detail?jobName=CalJob&jobGroup=DEFAULT">DEFAULT.CalJob</a></td>
		<td><a href="/main/job/trigger-detail?triggerName=CalJob1&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.CalJob1</a></td>

		<td></td>
		<td>11/25/11 17:00:00</td>
		<td class="action">
			<a href="/main/job/run-job?jobName=CalJob&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=CalJob1&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=CalJob1&triggerGroup=DEFAULT">Unschedule</a>
		</td>

	</tr>
	
	
	<tr>
		<td><a href="/main/job/job-detail?jobName=CalJob2&jobGroup=DEFAULT">DEFAULT.CalJob2</a></td>
		<td><a href="/main/job/trigger-detail?triggerName=CalJob2&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.CalJob2</a></td>
		<td></td>
		<td>11/25/11 17:00:00</td>
		<td class="action">

			<a href="/main/job/run-job?jobName=CalJob2&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=CalJob2&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=CalJob2&triggerGroup=DEFAULT">Unschedule</a>
		</td>
	</tr>
	
	
	<tr>
		<td><a href="/main/job/job-detail?jobName=CalJob3&jobGroup=DEFAULT">DEFAULT.CalJob3</a></td>

		<td><a href="/main/job/trigger-detail?triggerName=CalJob3&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.CalJob3</a></td>
		<td></td>
		<td>11/25/11 17:00:00</td>
		<td class="action">
			<a href="/main/job/run-job?jobName=CalJob3&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=CalJob3&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=CalJob3&triggerGroup=DEFAULT">Unschedule</a>

		</td>
	</tr>
	
	
	<tr>
		<td><a href="/main/job/job-detail?jobName=calendarIntervalJob&jobGroup=DEFAULT">DEFAULT.calendarIntervalJob</a></td>
		<td><a href="/main/job/trigger-detail?triggerName=calendarIntervalJob&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.calendarIntervalJob</a></td>
		<td></td>
		<td>02/25/12 16:00:18</td>

		<td class="action">
			<a href="/main/job/run-job?jobName=calendarIntervalJob&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=calendarIntervalJob&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=calendarIntervalJob&triggerGroup=DEFAULT">Unschedule</a>
		</td>
	</tr>
	
	
	<tr>

		<td><a href="/main/job/job-detail?jobName=cronJob&jobGroup=DEFAULT">DEFAULT.cronJob</a></td>
		<td><a href="/main/job/trigger-detail?triggerName=cronJob&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.cronJob</a></td>
		<td></td>
		<td>11/25/11 17:00:00</td>
		<td class="action">
			<a href="/main/job/run-job?jobName=cronJob&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=cronJob&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=cronJob&triggerGroup=DEFAULT">Unschedule</a>

		</td>
	</tr>
	
	
	<tr>
		<td><a href="/main/job/job-detail?jobName=dailyTimeIntervalJob&jobGroup=DEFAULT">DEFAULT.dailyTimeIntervalJob</a></td>
		<td><a href="/main/job/trigger-detail?triggerName=dailyTimeIntervalJob&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.dailyTimeIntervalJob</a></td>
		<td></td>
		<td>11/25/11 16:24:00</td>

		<td class="action">
			<a href="/main/job/run-job?jobName=dailyTimeIntervalJob&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=dailyTimeIntervalJob&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=dailyTimeIntervalJob&triggerGroup=DEFAULT">Unschedule</a>
		</td>
	</tr>
	
	
	<tr>

		<td><a href="/main/job/job-detail?jobName=simpleJob&jobGroup=DEFAULT">DEFAULT.simpleJob</a></td>
		<td><a href="/main/job/trigger-detail?triggerName=simpleJob&triggerGroup=DEFAULT&fireTimesCount=20">DEFAULT.simpleJob</a></td>
		<td></td>
		<td>11/25/11 17:00:11</td>
		<td class="action">
			<a href="/main/job/run-job?jobName=simpleJob&jobGroup=DEFAULT">Run It Now</a> |
			
			
			
				<a class="pause-trigger" href="/main/job/pauseTrigger?triggerName=simpleJob&triggerGroup=DEFAULT">Pause</a> |
			
			
			
			<a class="unschedule-trigger" href="/main/job/unschedule?triggerName=simpleJob&triggerGroup=DEFAULT">Unschedule</a>

		</td>
	</tr>
	
	</tbody>
</table>
		</div>
	</div>
</div>

<div id="footer">
	You are running <code><a href="http://code.google.com/p/myschedule">myschedule-2.4.0-SNAPSHOT</a></code> with  
	<code><a href="http://quartz-scheduler.org">quartz-2.1.0</a></code> | by 
	<a href="http://code.google.com/p/zemiandeng">Zemian Deng</a>
</div>

</body>
</html>
