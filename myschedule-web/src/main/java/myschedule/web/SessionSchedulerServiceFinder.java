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
	
	public QuartzSchedulerService findSchedulerService(HttpSession session) {
		SessionData data = getOrCreateSessionData(session);
		return getOrCreateSchedulerServiceSessionData(data);
	}
	
	protected QuartzSchedulerService getOrCreateSchedulerServiceSessionData(SessionData data) {;
		String configId = data.getCurrentSchedulerConfigId();
		if (configId == null) {
			List<String> allConfigIds = schedulerServiceRepo.getSchedulerServiceConfigIds();
			if (allConfigIds.size() < 1) {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
						"There is no scheduler service available in repository. Please create a scheduler config first.");
			} else {
				// Find the first initialized services.
				for (String configId2 : allConfigIds) {
					SchedulerService<?> ss2 = schedulerServiceRepo.getSchedulerService(configId2);
					if (ss2.isInited()) {
						configId = configId2;
						break;
					}
				}
				if (configId == null) {
					throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
						"There are scheduler service available in repository, but none are initialized.");
				}
			}
		}

		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
		data.setCurrentSchedulerName(st.getSchedulerName());
		data.setCurrentSchedulerConfigId(configId);
		return ss;
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
	
	public SchedulerService<?> switchSchedulerService(String configId, HttpSession session) {
		SessionData data = getOrCreateSessionData(session);
		String origConfigId = data.getCurrentSchedulerConfigId();

		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());

		data.setCurrentSchedulerName(st.getSchedulerName());
		data.setCurrentSchedulerConfigId(configId);
		
		logger.info("Switched scheduler service in session data from {} to {} ", origConfigId, configId);
		return ss;
	}
}
