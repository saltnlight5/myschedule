<!-- dashboard specific menu -->
<ul id="menu">
	<li><a href="${ mainPath }/dashboard/list">Dashboard</a></li>
	<li>		
		<span>Scheduler:</span> 
		<select>
		<c:forEach items="${ sessionData.schedulerServiceRepository.names }" var="name">
			<c:set var="selectedAttr" value=""/>
			<c:if test="${ name == sessionData.currentSchedulerName }">
				<c:set var="selectedAttr" value="selected=\"selected\""/>
			</c:if>
			<option value="${ name }" ${ selectedAttr }>${ name }</option>
		</c:forEach>
		<option value="create-new-scheduler">... Create New Scheduler</option>
		</select>
	</li>
</ul>