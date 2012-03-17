package myschedule.web.servlet.app.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.Setter;
import myschedule.service.ErrorCodeException;
import myschedule.service.SchedulerContainer;
import myschedule.service.SchedulerService;
import myschedule.web.servlet.ActionFilter;
import myschedule.web.servlet.ViewData;
import myschedule.web.session.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ensure MySchedule application session would contain the sesionData, or else redirect to Dashboard.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com> 
 */
public class SessionDataFilter implements ActionFilter {

	private static final Logger logger = LoggerFactory.getLogger(SessionDataFilter.class);

	@Setter
	private SchedulerContainer schedulerContainer;

	@Override
	public ViewData beforeAction(String actionPath, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ViewData viewData = null; // SUCESS (allow action handler to continue.)
		HttpSession session = req.getSession(true);
		if (session.getAttribute(SessionData.SESSION_DATA_KEY) == null) {
			try {
				SessionData sessionData = createSessionData(req);
				session.setAttribute(SessionData.SESSION_DATA_KEY, sessionData);
			} catch (ErrorCodeException e) {
				viewData = new ViewData("redirect:/dashboard/list", req, resp);
			}
		}
		return viewData;
	}

	@Override
	public void afterAction(ViewData viewData, String actionPath, HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		// Do nothing.
	}

	private SessionData createSessionData(HttpServletRequest req) {
		String configId = req.getParameter("configId");
		SchedulerService scheduler = null;
		if (configId == null) {
			scheduler = schedulerContainer.findFirstInitedScheduler();
		} else {
			scheduler = schedulerContainer.getSchedulerService(configId);
		}
		SessionData result = new SessionData();
		result.setScriptEngineName("JavaScript");
		result.setCurrentSchedulerName(scheduler.getScheduler().getSchedulerNameAndId());
		result.setCurrentSchedulerConfigId(scheduler.getConfigId());
		logger.info("New session data created: {}", result);
		return result;
	}
}
