<div id="submenu">
<ul>
<li><a href="${ mainPath }/job/list">Jobs with trigger</a></li>
<li><a href="${ mainPath }/job/list-no-trigger-jobs">Jobs without trigger</a></li>

<c:if test="${ sessionData.currentSchedulerStarted && !sessionData.currentSchedulerPaused}">
<li><a href="${ mainPath }/job/list-executing-jobs">Currently Executing Jobs</a></li>
</c:if>

<li><a href="${ mainPath }/job/load-xml">Load Jobs (Xml)</a></li>
</ul>
</div>