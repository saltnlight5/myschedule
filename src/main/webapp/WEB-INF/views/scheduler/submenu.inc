<div class="submenu">	
<ul>
<li><a href="${ mainPath }/scheduler/summary">Summary</a></li>
<li><a href="${ mainPath }/scheduler/detail">Detail</a></li>
<c:choose>
<c:when test="${ data.schedulerInStandbyMode }">
	<li><a href="${ mainPath }/scheduler/start">Resume Scheduler (Start Jobs)</a></li>
</c:when>
<c:otherwise>
	<li><a href="${ mainPath }/scheduler/standby">Pause Scheduler (StandBy Mode)</a></li>
</c:otherwise>
</c:choose>
</ul>
</div>