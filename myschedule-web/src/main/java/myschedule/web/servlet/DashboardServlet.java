package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;

public class DashboardServlet extends ViewDataServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected ViewData processRequest(HttpServletRequest req) {
		return ViewData.view("dashboard/list");
	}
	
}
