<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>
<%@ include file="/WEB-INF/views/job/submenu.inc" %>

<h1>Job Detail and Its Associated Triggers</h1>

<h2>Job : ${ data.jobDetail.fullName }</h2>
<table class="simple datalist">
	<tr><td>Name</td><td>${ data.jobDetail.name }</td></tr>
	<tr><td>Group</td><td>${ data.jobDetail.group }</td></tr>
	<tr><td>Job Class</td><td>${ data.jobDetail.jobClass }</td></tr>
	<tr><td>Key</td><td>${ data.jobDetail.key }</td></tr>
	<tr><td>Description</td><td>${ data.jobDetail.description }</td></tr>
	<tr><td>Durability</td><td>${ data.jobDetail.durable }</td></tr>
	<tr><td>Stateful</td><td>${ data.jobDetail.stateful }</td></tr>
	<tr><td>Volatile</td><td>${ data.jobDetail.volatile }</td></tr>
	<tr><td>Should Recover</td><td>${ data.jobDetailShouldRecover }</td></tr>
	
	<c:forEach items="${ data.jobDetail.jobDataMap }" var="item">
	<tr><td>Job Data Map: ${ item.key }</td><td>${ item.value }</td></tr>
	</c:forEach>
	
</table>

<c:forEach items="${ data.triggers }" var="trigger">
	<%@ include file="/WEB-INF/views/job/trigger-detail.inc" %>
</c:forEach>

<%@ include file="/WEB-INF/views/footer.inc" %>