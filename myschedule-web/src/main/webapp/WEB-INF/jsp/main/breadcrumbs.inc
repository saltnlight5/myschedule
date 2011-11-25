<ul id="breadcrumbs">
	<li><a class="breadcrumb_e breadcrumb_icon_home" href="${ mainPath }/index"></a></li>
	<li><a href="${ mainPath }/dashboard/index">Dashboard</a></li>
	
	<c:if test="${ not empty breadCrumbsScheduler }">
	<li><a href="${ mainPath }/job/index">${ sessionData.currentSchedulerName }</a></li>
	
	</c:if>
</ul>