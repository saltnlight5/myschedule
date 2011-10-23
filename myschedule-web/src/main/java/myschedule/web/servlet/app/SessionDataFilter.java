package myschedule.web.servlet.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Setter;
import myschedule.service.ErrorCodeException;
import myschedule.web.servlet.ActionFilter;
import myschedule.web.servlet.ViewData;
import myschedule.web.session.SessionSchedulerServiceFinder;

/**
 * Ensure MySchedule application session would contain the sesionData, or else redirect to Dashboard.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SessionDataFilter implements ActionFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionDataFilter.class);
	
	@Setter
	protected SessionSchedulerServiceFinder schedulerServiceFinder;

	@Override
	public ViewData beforeAction(String actionPath, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ViewData viewData = null; // SUCESS (allow action handler to continue.)
		HttpSession session = req.getSession(true);
		try {
			schedulerServiceFinder.findSchedulerService(session);
		} catch (ErrorCodeException e) {
			logger.debug("Failed to find or create a scheduler in session data, so will redirect to dashboard.");
			return ViewData.view("redirect:/dashboard/list");
		}
		return viewData;
	}

	@Override
	public void afterAction(ViewData viewData, String actionPath, HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		// Do nothing.
	}

}
