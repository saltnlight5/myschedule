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
		if (schedulerServiceName == null) {
			// If there is only one scheduler, then set session.
			List<String> names = schedulerServiceContainer.getSchedulerServiceNames();
			if (names.size() == 1) {
				schedulerServiceName = names.get(0);
				sessionData.setCurrentSchedulerName(schedulerServiceName);
				logger.info("There is only one scheduler in app, setting it to session data.");
			} else {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM,
						"The request require a scheduler service to be selected, but none is in session.");
			}
		}
		SchedulerService schedulerService = schedulerServiceContainer.getSchedulerService(schedulerServiceName);
		if (!schedulerService.isInitialized()) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
					"The scheduler service " + schedulerServiceName + " has not been initialized. Please select one that has properly initialized.");
		}
		return true; // continue process.
	}
}
