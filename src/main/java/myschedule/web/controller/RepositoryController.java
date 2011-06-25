package myschedule.web.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceFinder;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.ServiceContainer;
import myschedule.web.SessionData;

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
@RequestMapping(value="/repository")
public class RepositoryController {
	
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
		List<String> names = schedulerRepository.getNames();
		SchedulerServiceListPageData data = new SchedulerServiceListPageData();
		data.setNames(names);
		data.setSchedulerMetaDataMap(getSchedulerMetaDataMap(names));
		return new DataModelMap("data", data);
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


	protected Map<String, SchedulerMetaData> getSchedulerMetaDataMap(List<String> names) {
		Map<String, SchedulerMetaData> getSchedulerMetaDataMap = new HashMap<String, SchedulerMetaData>();
		for (String name : names) {
			SchedulerService schedulerService = schedulerRepository.getSchedulerService(name);
			getSchedulerMetaDataMap.put(name, schedulerService.getSchedulerMetaData());
		}
		return getSchedulerMetaDataMap;
	}

}
