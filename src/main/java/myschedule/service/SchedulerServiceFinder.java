package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_SERIVCE_NOT_FOUND;

import javax.servlet.http.HttpSession;

import myschedule.web.SessionData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide search and storing of SchedulerService instance in a web application env.
 *
 * @author Zemian Deng
 */
public class SchedulerServiceFinder {
	
	public static final String SESSION_DATA_KEY = "sessionData";

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SchedulerService defaultSchedulerService;
	
	public void setDefaultSchedulerService(SchedulerService defaultSchedulerService) {
		this.defaultSchedulerService = defaultSchedulerService;
	}
	
	public SchedulerService find(HttpSession session) {
		SessionData data = getOrCreateSessionData(session);
		SchedulerService result = findBySessionData(data);
		if (result == null) {
			// Not found, try the default scheduler.
			if (defaultSchedulerService == null)
				throw new ErrorCodeException(SCHEDULER_SERIVCE_NOT_FOUND, "Unable to find any scheduler service.");
			
			// We have a default scheduler
			result = defaultSchedulerService;
			logger.debug("Using default scheduler service.");
			
			// Need to update session with default scheduler.
			data.setCurrentSchedulerName(result.getName());
			logger.info("Added default scheduler service "+ result.getName() + " into session data.");
		}
		
		logger.debug("Found scheduler service: " + result.getName());
		return result;
	}
	
	private SchedulerService findBySessionData(SessionData data) {
		String name = data.getCurrentSchedulerName();
		if (name == null) {
			return null;
		}
		SchedulerServiceRepository repository = SchedulerServiceRepository.getInstance();
		SchedulerService schedulerService = null;
		
		// We might have multiple threads access this part of code from controller.
		synchronized(repository) {
			if (repository.hasSchedulerService(name)) {
				schedulerService = repository.getSchedulerService(name);
			}
		}
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
			setSessionData(session, data);
		}
		return data;
	}
	
	protected void setSessionData(HttpSession session, SessionData data) {
		session.setAttribute(SESSION_DATA_KEY, data);
	}
	
	public SchedulerService switchSchedulerService(String newSchedulerName, HttpSession session) {
		SchedulerServiceRepository repository = SchedulerServiceRepository.getInstance();
		SchedulerService schedulerService = repository.getSchedulerService(newSchedulerName);
		String currentSchedulerName = schedulerService.getName();
		SessionData data = getOrCreateSessionData(session);
		data.setCurrentSchedulerName(currentSchedulerName);
		setSessionData(session, data);
		return schedulerService;
	}
}
