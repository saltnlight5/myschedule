package myschedule.web.session;

import static myschedule.web.session.SessionData.SESSION_DATA_KEY;

import java.util.List;

import javax.servlet.http.HttpSession;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide search and storing of QuartzSchedulerService instance in a web application env.
 *
 * @author Zemian Deng
 */
public class SessionSchedulerServiceFinder {	
	
	private static final Logger logger = LoggerFactory.getLogger(SessionSchedulerServiceFinder.class);
	
	protected SchedulerServiceRepository schedulerServiceRepo;
	
	public void setSchedulerServiceRepo(SchedulerServiceRepository schedulerServiceRepo) {
		this.schedulerServiceRepo = schedulerServiceRepo;
	}
	
	public QuartzSchedulerService findSchedulerService(HttpSession session) {
		SessionData data = getOrCreateSessionData(session);
		String configId = data.getCurrentSchedulerConfigId();
		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		return ss;
	}
	
	protected SessionData createSessionData() {
		String configId = null;

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

		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());

		SessionData result = new SessionData();
		result.setScriptEngineName("JavaScript");
		result.setCurrentSchedulerName(st.getSchedulerName());
		result.setCurrentSchedulerConfigId(configId);		
		logger.info("New session data created: {}", result);
		return result;
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
