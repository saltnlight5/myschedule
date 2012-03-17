<div id="submenu">	
<ul>
<li><a href="${ mainPath }/scheduler/summary">Summary</a></li>
<li><a href="${ mainPath }/scheduler/detail">Detail</a></li>
<li><a href="${ mainPath }/scheduler/listeners">Listeners</a></li>
<li><a href="${ mainPath }/scheduler/modify">Config Properties</a></li>
<c:choose><c:when test="${ data.isStandby }">
	<li><a href="${ mainPath }/scheduler/start">Start Scheduler</a></li>
</c:when><c:otherwise>
	<li><a href="${ mainPath }/scheduler/standby">Standby Scheduler</a></li>
	<li><a href="${ mainPath }/scheduler/pause-all-triggers">Pause All Triggers</a></li>
	<li><a href="${ mainPath }/scheduler/resume-all-triggers">Resume All Triggers</a></li>
</c:otherwise></c:choose>

</ul>
</div><!-- div.submenu -->