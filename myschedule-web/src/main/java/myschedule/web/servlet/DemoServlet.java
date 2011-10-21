package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;


/**
 *

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

//	@Override
//	protected ViewData processRequest(HttpServletRequest req) {
//		logger.info("Just a demo");
//		return ViewData.viewData("demo/test", "message", "ServerTime=" + new java.util.Date());
//	}
	
	public DemoServlet() {
		addActionHandler("/demo", new SimpleActionHandler("/demo/index"));
		addActionHandler("/demo/test", testAction);
	}
	
	ActionHandler testAction = new SimpleActionHandler() {
		@Override
		protected void processAction(String actionPath, ViewData viewData) {
			viewData.addData("message", "ServerTime=" + new java.util.Date());
		}			
	};
}
