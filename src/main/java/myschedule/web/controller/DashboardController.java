package myschedule.web.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * Scheduler repository controller - managing multiple of {@link SchedulerService}.
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
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:list";
	}

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
	
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public DataModelMap delete(
			@RequestParam String name,
			HttpSession session) {
		// Remove from repo
		SchedulerService removedSchedulerService = schedulerRepository.remove(name);
				
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
				logger.warn("There is no more scheduler service left in repository!.");
			}
		}
		
		logger.info("Scheduler service " + name + " remmoved.");
		return new DataModelMap("removedSchedulerService", removedSchedulerService);
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public DataModelMap create() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="/create-action", method=RequestMethod.POST)
	public DataModelMap createAction(
			@RequestParam URL configUrl,
			@RequestParam boolean autoStart,
			@RequestParam boolean waitForJobsToComplete,
			HttpSession session) {
		SchedulerService schedulerService = new SchedulerService();
		schedulerService.setConfigUrl(configUrl);
		schedulerService.setAutoStart(autoStart);
		schedulerService.setWaitForJobsToComplete(waitForJobsToComplete);
		serviceContainer.addAndInitService(schedulerService);
		logger.info("New schedulerService " + configUrl + " has been created.");
		return new DataModelMap("schedulerService", schedulerService);
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

}
