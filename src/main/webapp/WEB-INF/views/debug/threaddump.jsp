<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%
Comparator<Thread> threadComparator = new Comparator<Thread>()
{
	public int compare(Thread o1, Thread o2)
	{
		int result = o1.getName().compareTo(o2.getName());
		if (result == 0) {
			Long id1 = o1.getId();
			Long id2 = o2.getId();
			return id1.compareTo(id2);
		}
		return result;
	}
};
Map<Thread, StackTraceElement[]> traces = new TreeMap<Thread, StackTraceElement[]>(threadComparator);
traces.putAll(Thread.getAllStackTraces());
%>
<html>
<body>
<h2>Thread Summary:</h2>
<table cellpadding="5" cellspacing="5">
  <tr>
    <th>Thread</th>
    <th>State</th>
    <th>Priority</th>
    <th>Daemon</th>
  </tr>
  <c:forEach items="${threadDump.threads}" var="thr">
    <tr>
      <td><a href="#${thr.id}">${thr.name}</a></td>
      <td>${thr.state}</td>
      <td>${thr.priority}</td>
      <td>${thr.daemon}</td>
    </tr>
  </c:forEach>
</table>

<h2>Stack Trace of JVM:</h2>
<c:forEach items="${threadDump.traces}" var="trace">
  <h4><a name="${trace.key.id}">${trace.key}</a></h4>
  <pre>
  <c:forEach items="${trace.value}" var="traceline">
      at ${traceline}</c:forEach>
  </pre>
</c:forEach>

</body>
</html>
