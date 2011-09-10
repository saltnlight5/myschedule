package myschedule.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.quartz.SchedulerTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide search and storing of QuartzSchedulerService instance in a web application env.
 *
 * @author Zemian Deng
 */
public class SessionSchedulerServiceFinder {	
	
	private static final Logger logger = LoggerFactory.getLogger(SessionSchedulerServiceFinder.class);
	
	public static final String SESSION_DATA_KEY = "sessionData";

	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
		
	public QuartzSchedulerService find(HttpSession session) {
		SessionData data = getOrCreateSessionData(session);
		return findBySessionData(data);
	}
	
	public SchedulerTemplate findSchedulerTemplate(HttpSession session) {
		QuartzSchedulerService schedulerService = find(session);
		if (schedulerService == null)
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
					"Unable to find a scheduler service in user session data.");
		return new SchedulerTemplate(schedulerService.getScheduler());
	}
	
	protected QuartzSchedulerService findBySessionData(SessionData data) {
		List<String> allSchedulerServiceNames = schedulerServiceRepo.getSchedulerServiceNames();
		String schedulerServiceName = data.getCurrentSchedulerServiceName();
		if (schedulerServiceName == null) {
			if (allSchedulerServiceNames.size() < 1) {
				return null;
			} else {
				schedulerServiceName = allSchedulerServiceNames.get(0);
			}
		}
		
		QuartzSchedulerService schedulerService = (QuartzSchedulerService)schedulerServiceRepo.getSchedulerService(schedulerServiceName);
		data.setCurrentSchedulerServiceName(schedulerServiceName);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerService.getScheduler());
		data.setCurrentSchedulerPaused(schedulerTemplate.isInStandbyMode());
		data.setCurrentSchedulerStarted(schedulerTemplate.isStarted() && !schedulerTemplate.isInStandbyMode());
		return schedulerService;
	}

	protected SessionData createSessionData() {
		SessionData data = new SessionData();
		logger.info("New session data created: " + data);
		return data;
	}
	
	protected boolean hasSessionData(HttpSession session) {
		return session.getAttribute(SESSION_DATA_KEY) != null;
	}
	
	public SessionData getOrCreateSessionData(HttpSession session) {
		SessionData data = (SessionData)session.getAttribute(SESSION_DATA_KEY);
		if (data == null) {
			data = createSessionData();
			session.setAttribute(SESSION_DATA_KEY, data);
		}
		return data;
	}
	
	public SchedulerService<?> switchSchedulerService(String newSchedulerName, HttpSession session) {
		SessionData data = getOrCreateSessionData(session);
		String currentSchedulerName = data.getCurrentSchedulerServiceName();
		QuartzSchedulerService schedulerService = (QuartzSchedulerService)schedulerServiceRepo.getSchedulerService(newSchedulerName);
		data.setCurrentSchedulerServiceName(newSchedulerName);
		session.setAttribute(SESSION_DATA_KEY, data);
		logger.info("Switched scheduler service in session data from {} to {} ", currentSchedulerName, newSchedulerName);
		return schedulerService;
	}
}
