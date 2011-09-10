package myschedule.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myschedule.service.QuartzSchedulerService;
import myschedule.web.SessionSchedulerServiceFinder;
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
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// Ensure session data is created.
		HttpSession session = request.getSession(true); // create session if not exists.
		QuartzSchedulerService schedulerService = schedulerServiceFinder.find(session);
				
		// If scheduler is not initialized and request is for url=/job/*, then we need to redirect 
		// new url=job/scheduler-down instead.
		if (!schedulerService.isInited()) {
			String mainPath = WebAppContextListener.MAIN_PATH;
			String url = request.getRequestURI();
			String contextPath = request.getContextPath();
			if (url.endsWith("/job/scheduler-down")) { // ensure we are not in a infinite loop.
				return true;
			} else if (url.startsWith(contextPath + mainPath + "/job")) {
				response.sendRedirect(contextPath + mainPath + "/job/scheduler-down");
				return false;
			}
		}
		
		return true; // continue process.
	}
}
