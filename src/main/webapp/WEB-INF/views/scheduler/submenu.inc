<div id="submenu">	
<ul>
<li><a href="${ mainPath }/scheduler/summary">Summary</a></li>
<li><a href="${ mainPath }/scheduler/detail">Detail</a></li>
<c:choose>
<c:when test="${ data.schedulerInStandbyMode }">
	<li><a href="${ mainPath }/scheduler/start">Resume Scheduler</a></li>
</c:when>
<c:otherwise>
	<li><a href="${ mainPath }/scheduler/standby">Pause Scheduler</a></li>
</c:otherwise>
</c:choose>
</ul>
</div><!-- div.submenu -->