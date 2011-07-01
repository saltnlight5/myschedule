package myschedule.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.Quartz18SchedulerService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceContainer;
import myschedule.service.SchedulerServiceDao;
import myschedule.service.SchedulerServiceFinder;
import myschedule.service.ServiceContainer;
import myschedule.web.SessionData;
import myschedule.web.WebAppContextListener;
import myschedule.web.controller.SchedulerStatusListPageData.SchedulerStatus;

import org.apache.commons.io.IOUtils;
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
	
	@Autowired @Qualifier("schedulerServiceContainer")
	protected SchedulerServiceContainer schedulerServiceContainer;

	@Autowired @Qualifier("schedulerServiceFileDao")
	protected SchedulerServiceDao schedulerServiceDao;	
		
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
			@RequestParam String configPropsText,
			HttpSession session) {
		// Parse form config into props
		Properties configProps = loadPropertiesFromText(configPropsText);
		
		// Create a new SchedulerService.
		Quartz18SchedulerService schedulerService = new Quartz18SchedulerService();
		schedulerService.setConfigProps(configProps);
		schedulerServiceContainer.addAndInitSchedulerService(schedulerService);
		logger.info("New schedulerService " + schedulerService.getName() + " has been created.");
		
		// Saving the config to storage
		schedulerServiceDao.saveSchedulerService(schedulerService);
		logger.info("New schedulerService " + schedulerService.getName() + " configProps has been saved.");
		
		return new DataModelMap("schedulerService", schedulerService);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public DataModelMap delete() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="/delete-action", method=RequestMethod.POST)
	public DataModelMap deleteAction(
			@RequestParam String name,
			HttpSession session) {

		// Check to see if needs to remove from the finder service
		SchedulerService defSchedulerService = schedulerServiceFinder.getDefaultSchedulerService();
		if (name.equals(defSchedulerService.getName())) {
			schedulerServiceFinder.setDefaultSchedulerService(null);
			logger.info("Removed scheduler service in finder service.");
		}
		
		// Stop and destroy the service - it will auto remove from repository!
		schedulerServiceContainer.removeAndDestroySchedulerService(name);
		logger.info("Scheduler service " + name + " has destroyed.");

		// Removing the config from storage
		schedulerServiceDao.deleteSchedulerService(name);
		logger.info("The schedulerService " + name + " configProps has been removed.");
				
		// Check to see if needs to remove from session
		SessionData sessionData = schedulerServiceFinder.getOrCreateSessionData(session);
		String currentName = sessionData.getCurrentSchedulerName();
		if (name.equals(currentName)) {
			// Update session with first name in list, or clear it.
			List<String> names = schedulerServiceContainer.getSchedulerServiceNames();
			if (names.size() > 0) {
				String newName = names.get(0);
				sessionData.setCurrentSchedulerName(newName);
				logger.info("Switched to another scheduler service in session data: " + newName);
			} else { 
				sessionData.setCurrentSchedulerName(null);
				logger.warn("There is no more scheduler service left! Removed scheduler from session data.");
			}
		}
		
		return new DataModelMap("removedName", name);
	}
	
	@RequestMapping(value="/get-config-eg", method=RequestMethod.GET)
	public void getConfigExample(@RequestParam String name, Writer writer) {
		logger.debug("Getting resource: " + name);
		ClassLoader classLoader = getClassLoader();
		InputStream inStream = classLoader.getResourceAsStream("myschedule/spring/scheduler/" + name);
		try {
			IOUtils.copy(inStream, writer);
			inStream.close();
			writer.flush();
			writer.close();
			logger.info("Returned text for resource: " + name);
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to get resource " + name, e);
		}
	}

	protected ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	protected List<SchedulerStatus> getSchedulerStatusList() {
		List<SchedulerStatus> result = new ArrayList<SchedulerStatus>();
		List<String> names = schedulerServiceContainer.getSchedulerServiceNames();
		for (String name : names) {
			SchedulerService sservice = schedulerServiceContainer.getSchedulerService(name);			
			SchedulerMetaData smeta = sservice.getSchedulerMetaData();
			SchedulerStatus sstatus = new SchedulerStatus();
			sstatus.setName(name);
			sstatus.setJobStorageType(smeta.getJobStoreClass().getName());
			sstatus.setRunning(sservice.isJobRunning());
			result.add(sstatus);
		}
		return result;
	}

	protected Properties loadPropertiesFromText(String configPropsText) {
		try {
			Properties config = new Properties();
			// Read in form input.
			StringReader reader = new StringReader(configPropsText);
			config.load(reader);
			return config;
		} catch (Exception e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM,  "Failed to create configProps from input text.", e);			
		}					
	}
}
