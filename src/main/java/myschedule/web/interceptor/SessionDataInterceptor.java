package myschedule.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myschedule.service.SchedulerServiceFinder;
import myschedule.service.SchedulerServiceRepository;
import myschedule.web.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionDataInterceptor extends HandlerInterceptorAdapter {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SchedulerServiceFinder schedulerServiceFinder;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// Ensure session data is created.
		HttpSession session = request.getSession(true); // create session if not exists.
		SessionData sessionData = schedulerServiceFinder.getOrCreateSessionData(session);
		if (sessionData.getCurrentSchedulerName() == null) {
			List<String> names = SchedulerServiceRepository.getInstance().getNames();
			if (names.size()  > 0) {
				sessionData.setCurrentSchedulerName(names.get(0)); // Use first one in list.
			} else {
				logger.warn("No scheduler service found in repository!");
			}
		}
		return true; // continue process.
	}
}
