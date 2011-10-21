package myschedule.web.servlet.app;

import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.ViewDataActionHandler;

/**
 * A servlet that handles dashboard actions.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class DashboardServlet extends ActionHandlerServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		addActionHandler("", new ViewDataActionHandler());
	}	
}
