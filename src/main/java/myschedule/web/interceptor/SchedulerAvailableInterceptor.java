package myschedule.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceContainer;
import myschedule.service.SchedulerServiceFinder;
import myschedule.web.SessionData;
import myschedule.web.WebAppContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Ensure requested URL have a current scheduler service in session data and it's initialized!
 *
 * @author Zemian Deng
 */
public class SchedulerAvailableInterceptor extends HandlerInterceptorAdapter {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SchedulerServiceFinder schedulerServiceFinder;
	
	@Autowired @Qualifier("schedulerServiceContainer")
	protected SchedulerServiceContainer schedulerServiceContainer;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// Ensure session data is created.
		HttpSession session = request.getSession(true); // create session if not exists.
		SessionData sessionData = schedulerServiceFinder.getOrCreateSessionData(session);
		String schedulerServiceName = sessionData.getCurrentSchedulerName();
		SchedulerService schedulerService;
		if (schedulerServiceName == null) {
			// If there is only one scheduler, then set session.
			List<String> names = schedulerServiceContainer.getSchedulerServiceNames();
			if (names.size() > 0) {
				schedulerService = schedulerServiceFinder.find(session);
			} else {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM,
						"The request require a scheduler service to be selected, but none is in session.");
			}
		} else {
			schedulerService = schedulerServiceContainer.getSchedulerService(schedulerServiceName);
		}
		
		if (!schedulerService.isInit()) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
					"The scheduler service " + schedulerServiceName + " has not been initialized. Please select one that has properly initialized.");
		}
		
		// If scheduler is down and req is for /job/*, then redirect to job/scheduler-down instead.
		if (schedulerService.isShutdown()) {
			String mainPath = WebAppContextListener.MAIN_PATH;
			String url = request.getRequestURI();
			String contextPath = request.getContextPath();
			if (url.endsWith("/job/scheduler-down")) { // ensure we are not in a infinite loop.
				return true;
			} else if (url.startsWith(contextPath + mainPath + "/job")) {
				response.sendRedirect(contextPath + mainPath + "/job/scheduler-down");
				return false;
			}
		} else {
			// Update current scheduler state
			sessionData.setCurrentSchedulerStarted(schedulerService.isStarted());
			sessionData.setCurrentSchedulerPaused(schedulerService.isPaused());
		}
		
		return true; // continue process.
	}
}
