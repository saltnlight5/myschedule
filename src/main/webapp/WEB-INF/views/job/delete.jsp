<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.inc" %>

<h1>Delete ${ data.type } : ${ data.name } ${ data.group }</h1>
<p id="message">${ data.message }</p>

<%@ include file="/WEB-INF/views/footer.inc" %>