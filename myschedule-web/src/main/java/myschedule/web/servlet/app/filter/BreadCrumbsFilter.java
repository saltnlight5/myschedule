package myschedule.web.servlet.app.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myschedule.web.servlet.ActionFilter;
import myschedule.web.servlet.ViewData;

/**
 * Add request attribute flag to indicate breadcrums path.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class BreadCrumbsFilter implements ActionFilter {

	@Override
	public ViewData beforeAction(String actionPath, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ViewData result = null; // Pass to next filter or action handler.
		if ( "/index".equals(actionPath)) {
			// Do nothing.
		} else {
			req.setAttribute("breadCrumbsScheduler", true);
		}
		return result;
	}

	@Override
	public void afterAction(ViewData viewData, String actionPath, HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		// Do nothing.
	}

}
