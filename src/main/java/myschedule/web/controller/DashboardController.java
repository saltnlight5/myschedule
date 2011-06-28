package myschedule.web.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceFinder;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.ServiceContainer;
import myschedule.web.SessionData;
import myschedule.web.WebAppContextListener;
import myschedule.web.controller.SchedulerStatusListPageData.SchedulerStatus;

import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * Scheduler Dashboard Controller - managing multiple of scheduler services.
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/dashboard")
public class DashboardController {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired @Qualifier("serviceContainer")
	protected ServiceContainer serviceContainer;
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SchedulerServiceFinder schedulerServiceFinder;
	
	protected SchedulerServiceRepository schedulerRepository = SchedulerServiceRepository.getInstance();
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public DataModelMap list() {
		SchedulerStatusListPageData data = new SchedulerStatusListPageData();
		data.setSchedulerStatusList(getSchedulerStatusList());
		return new DataModelMap(data);
	}

	@RequestMapping(value="/switch-scheduler", method=RequestMethod.GET)
	public String switchScheduler(
			@RequestParam String name,
			HttpSession session) {
		schedulerServiceFinder.switchSchedulerService(name, session);
		String mainPath = WebAppContextListener.MAIN_PATH;
		return "redirect:" + mainPath + "/job/list";
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public DataModelMap create() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="/create-action", method=RequestMethod.POST)
	public DataModelMap createAction(
			@ModelAttribute SchedulerServiceForm form,
			HttpSession session) {		
		try {
			logger.info("Create scheduler service form " + form);
			URL configUrl = new URL(form.getConfigUrl());
			SchedulerService schedulerService = new SchedulerService();
			schedulerService.setConfigUrl(configUrl);
			schedulerService.setAutoStart(form.isAutoStart());
			schedulerService.setWaitForJobsToComplete(form.isWaitForJobsToComplete());
			serviceContainer.addAndInitService(schedulerService);
			logger.info("New schedulerService " + configUrl + " has been created.");
			return new DataModelMap("schedulerService", schedulerService);
		} catch (MalformedURLException e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to create config URL using " + form.getConfigUrl(), e);
		}
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public DataModelMap delete() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="/delete-action", method=RequestMethod.POST)
	public DataModelMap deleteAction(
			@RequestParam String name,
			HttpSession session) {
		// Remove from repo
		SchedulerService removedSchedulerService = schedulerRepository.remove(name);
		logger.info("Scheduler service " + name + " removed from repository.");
		
		// Check to see if needs to remove from the finder service
		SchedulerService defSchedulerService = schedulerServiceFinder.getDefaultSchedulerService();
		if (name.equals(defSchedulerService.getName())) {
			schedulerServiceFinder.setDefaultSchedulerService(null);
			logger.info("Removed scheduler matched default scheduler service in finder service. Removed its reference as well.");
		}
		
		// Check to see if needs to remove from session
		SessionData sessionData = schedulerServiceFinder.getOrCreateSessionData(session);
		String currentName = sessionData.getCurrentSchedulerName();
		if (name.equals(currentName)) {
			// Update session with first name in list, or clear it.
			List<String> names = schedulerRepository.getNames();
			if (names.size() > 0) {
				String newName = names.get(0);
				sessionData.setCurrentSchedulerName(newName);
				logger.info("Updated session data current scheduler name: " + newName);
			} else { 
				sessionData.setCurrentSchedulerName(null);
				logger.warn("There is no more scheduler service left in repository! Removed scheduler from session data.");
			}
		}
		
		return new DataModelMap("removedSchedulerService", removedSchedulerService);
	}

	protected List<SchedulerStatus> getSchedulerStatusList() {
		List<SchedulerStatus> result = new ArrayList<SchedulerStatus>();
		List<String> names = schedulerRepository.getNames();
		for (String name : names) {
			SchedulerService sservice = schedulerRepository.getSchedulerService(name);
			SchedulerMetaData smeta = sservice.getSchedulerMetaData();
			String configPath = sservice.getConfigUrl() == null ? "UNKOWN_CONFIG_PROPS" : sservice.getConfigUrl().toString();
			SchedulerStatus sstatus = new SchedulerStatus();
			sstatus.setName(name);
			sstatus.setConfigPath(configPath);
			sstatus.setJobStorageType(smeta.getJobStoreClass().getName());
			sstatus.setRunning(smeta.isStarted() && !smeta.isInStandbyMode());
			result.add(sstatus);
		}
		return result;
	}

	public static class SchedulerServiceForm {
		protected String configUrl;
		protected boolean autoStart;
		protected boolean waitForJobsToComplete;
		public String getConfigUrl() {
			return configUrl;
		}
		public void setConfigUrl(String configUrl) {
			this.configUrl = configUrl;
		}
		public boolean isAutoStart() {
			return autoStart;
		}
		public void setAutoStart(boolean autoStart) {
			this.autoStart = autoStart;
		}
		public boolean isWaitForJobsToComplete() {
			return waitForJobsToComplete;
		}
		public void setWaitForJobsToComplete(boolean waitForJobsToComplete) {
			this.waitForJobsToComplete = waitForJobsToComplete;
		}
	}
	
}
