package myschedule.web.servlet.app;

import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;



/**
 * Just a demo. Not in use for project.

 * <p>Here are some general servlet request infromation:
 * <pre>
Req getServletPath: /dashboard
Req getRequestURI: /myschedule-web/dashboard
Req getRequestURL: http://localhost:8080/myschedule-web/dashboard

Req getServletPath: /dashboard
Req getRequestURI: /myschedule-web/dashboard/foo
Req getRequestURL: http://localhost:8080/myschedule-web/dashboard/foo

 * </pre>


 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class DemoServlet extends ActionHandlerServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		addActionHandler("", new ViewDataActionHandler());
		addActionHandler("/test", testAction);
	}
	
	protected ActionHandler testAction = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			viewData.addData("message", "ServerTime=" + new java.util.Date());
		}			
	};
}
