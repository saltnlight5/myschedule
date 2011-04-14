
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>
<h1>Request Information</h1>
<table cellpadding="5" cellspacing="5">

  <tr>
    <td>Servlet Major Version</td><td><%= application.getMajorVersion() %></td>
  </tr>
  <tr>
    <td>Servlet Minor Version</td><td><%= application.getMinorVersion() %></td>
  </tr>

  <tr>
    <td>method</td><td>${pageContext.request.method}</td>
  </tr>
  <tr>
    <td>contextPath</td><td>${pageContext.request.contextPath}</td>
  </tr>
  <tr>
    <td>servletPath</td><td>${pageContext.request.servletPath}</td>
  </tr>
  <tr>
    <td>requestURI</td><td>${pageContext.request.requestURI}</td>
  </tr>
  <tr>
    <td>requestURL</td><td>${pageContext.request.requestURL}</td>
  </tr>
  <tr>
    <td>characterEncoding</td><td>${pageContext.request.characterEncoding}</td>
  </tr>
  <tr>
    <td>contentType</td><td>${pageContext.request.contentType}</td>
  </tr>
  <tr>
    <td>contentLength</td><td>${pageContext.request.contentLength}</td>
  </tr>
  <tr>
    <td>locale</td><td>${pageContext.request.locale}</td>
  </tr>
  <tr>
    <td>userPrincipal</td><td>${pageContext.request.userPrincipal}</td>
  </tr>
</table>

</body>
</html>
