package myschedule.web.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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

import org.apache.commons.io.IOUtils;
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
		String fileLocationPrefix = "/data/myschedule"; // default to unix path
		if (System.getProperty("os.name").startsWith("Windows")) {
			fileLocationPrefix = "/C:/data/myschedule";
		}
		return new DataModelMap("fileLocationPrefix", fileLocationPrefix);
	}
	
	@RequestMapping(value="/create-action", method=RequestMethod.POST)
	public DataModelMap createAction(
			@ModelAttribute SchedulerServiceForm form,
			HttpSession session) {
		logger.info("Creating scheduler service form " + form);
		
		// Ensure we do not overwrite existing file.
		String fileLocation = form.getFileLocation();
		File fileLocationFile = new File(fileLocation);
		if (fileLocationFile.exists())
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "File location " + fileLocation + " already exists.");
		
		// Parse form config into props
		Properties configProps = loadPropertiesFromString(form.getConfigProperties());			
		
		// Ensure scheduler name is not already taken
		String schedulerName = configProps.getProperty("org.quartz.scheduler.instanceName", "QuartzScheduler");
		if (schedulerRepository.hasSchedulerService(schedulerName))
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Scheduler name alrady exists: " + schedulerName);
		
		// Save config and create a new SchedulerService.
		URL configUrl = savePropertiesToFile(configProps, fileLocationFile);
		SchedulerService schedulerService = new SchedulerService();
		schedulerService.setConfigUrl(configUrl);
		schedulerService.setAutoStart(form.isAutoStart());
		schedulerService.setWaitForJobsToComplete(form.isWaitForJobsToComplete());
		serviceContainer.addAndInitService(schedulerService);
		logger.info("New schedulerService " + configUrl + " has been created.");
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
			logger.info("Removed scheduler matched default scheduler service in finder service. Removed its reference as well.");
		}
		
		// Stop and destroy the service - it will auto remove from repository!
		SchedulerService schedulerService = schedulerRepository.getSchedulerService(name);
		schedulerService.stop();
		schedulerService.destroy();		
		logger.info("Scheduler service " + name + " has destroyed and removed from repository.");
		
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

	protected Properties loadPropertiesFromString(String configProperties) {
		try {
			Properties config = new Properties();
			// Read in form input.
			StringReader reader = new StringReader(configProperties);
			config.load(reader);
			return config;
		} catch (Exception e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
					"Failed to create Properties from input config.", e);			
		}					
	}

	protected URL savePropertiesToFile(Properties props, File file) {
		try {				
			Writer writer = new FileWriter(file);
			props.store(writer, "Scheduler Configuration Generated from MySchedule WebUI. CreateDate=" + new Date());
			writer.close();
			URL result = file.toURI().toURL();
			logger.info("New config file has successfully saved to " + file);
			return result;
		} catch (Exception e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
					"Failed to write config to " + file + ". Please ensure directories exist and has write permission.");			
		}
	}
}
