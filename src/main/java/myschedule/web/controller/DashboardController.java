package myschedule.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
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
		
		// Saving the new config to storage
		schedulerServiceDao.saveSchedulerService(schedulerService, configPropsText, false);
		logger.info("New schedulerService " + schedulerService.getName() + " configProps has been saved.");
		
		return new DataModelMap("schedulerService", schedulerService);
	}
	
	@RequestMapping(value="/modify-get-names", method=RequestMethod.GET)
	public DataModelMap modifyGetNames(HttpSession session) {
		List<String> schedulerNames = schedulerServiceContainer.getSchedulerServiceNames();
		return new DataModelMap("schedulerNames", schedulerNames);
	}
	
	@RequestMapping(value="/modify", method=RequestMethod.GET)
	public DataModelMap modify(
			@RequestParam String name, 
			HttpSession session) {
		SchedulerService schedulerService = schedulerServiceContainer.getSchedulerService(name);
		Properties configProps = schedulerService.getConfigProps();
		if (configProps == null || !schedulerService.isConfigModifiable())
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "ScheduleService " + name + " config props is not editiable.");
		String configPropsText = null;
		String schedulerServiceName = schedulerService.getSchedulerName();
		if (schedulerServiceDao.hasSchedulerService(schedulerServiceName)) {
			configPropsText = schedulerServiceDao.getConfigPropsText(schedulerServiceName);
		} else {
			configPropsText = propsToText(configProps);
		}
		
		DataModelMap data = new DataModelMap();
		data.addData("configPropsText", configPropsText);
		data.addData("schedulerServiceName", schedulerServiceName);
		return data; 
	}
	
	@RequestMapping(value="/modify-action", method=RequestMethod.POST)
	public DataModelMap modifyAction(
			@RequestParam String schedulerServiceName,
			@RequestParam String configPropsText,
			HttpSession session) {
		// Parse form config into props
		Properties configProps = loadPropertiesFromText(configPropsText);
		
		// Create a new SchedulerService.
		SchedulerService schedulerService = schedulerServiceContainer.getSchedulerService(schedulerServiceName);
		boolean origRunning = schedulerService.isStarted() && !schedulerService.isPaused();
		if (schedulerService.isInitialized()) {
			schedulerService.shutdown();
			schedulerService.destroy();
		}		
		schedulerService.setConfigProps(configProps);
		schedulerService.init();
		logger.info("SchedulerService " + schedulerServiceName + " has been updated and re-initialized.");
		
		// Update storage config
		schedulerServiceDao.saveSchedulerService(schedulerService, configPropsText, true);
		logger.info("SchedulerService " + schedulerService.getName() + " configProps has been updated.");
		
		if (origRunning) {
			schedulerService.start();
			logger.info("SchedulerService " + schedulerServiceName + " has been re-started.");
		}

		DataModelMap data = new DataModelMap();
		data.addData("schedulerService", schedulerService);
		data.addData("origRunning", origRunning);
		
		return data;
	}

	protected String propsToText(Properties configProps) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		// print in sorted order.
		List<String> names = new ArrayList<String>(configProps.stringPropertyNames());
		Collections.sort(names);
		for (String name : names)
			pWriter.println(name + " = " + configProps.getProperty(name));
		pWriter.close();
		return sWriter.toString();
	}

	@RequestMapping(value="/delete-get-names", method=RequestMethod.GET)
	public DataModelMap deleteGetNames() {
		List<String> schedulerNames = schedulerServiceContainer.getSchedulerServiceNames();
		return new DataModelMap("schedulerNames", schedulerNames);
	}
	
	@RequestMapping(value="/delete-action", method=RequestMethod.POST)
	public DataModelMap deleteAction(
			@RequestParam String name,
			HttpSession session) {

		// Check to see if needs to remove from the finder service
		SchedulerService defSchedulerService = schedulerServiceFinder.getDefaultSchedulerService();
		if (defSchedulerService != null && defSchedulerService.getName().equals(name)) {
			schedulerServiceFinder.setDefaultSchedulerService(null);
			logger.info("Removed scheduler service in finder service.");
		}
		
		// Stop and destroy the service and remove from the container
		schedulerServiceContainer.removeAndDestroySchedulerService(name);
		logger.info("Scheduler service " + name + " has destroyed.");

		// Removing the config from storage
		if (schedulerServiceDao.hasSchedulerService(name)) {
			schedulerServiceDao.deleteSchedulerService(name);
			logger.info("The schedulerService " + name + " configProps has been removed.");
		}
		
		// Check to see if needs to remove from session
		SessionData sessionData = schedulerServiceFinder.getOrCreateSessionData(session);
		String currentName = sessionData.getCurrentSchedulerName();
		if (name.equals(currentName)) {
			// Update session with first name in list, or clear it.
			List<String> names = schedulerServiceContainer.getInitializedSchedulerServiceNames();
			if (names.size() > 0) {
				String newName = names.get(0);
				sessionData.setCurrentSchedulerName(newName);
				logger.info("Session data is removed and switch to new scheduler name: " + newName);
			} else { 
				sessionData.setCurrentSchedulerName(null);
				logger.warn("Session data is removed. Note there is not more scheduler in container!");
			}
		}
		// save data for view page
		return new DataModelMap("removedSchedulerName", name);
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
			SchedulerStatus sstatus = new SchedulerStatus();
			sstatus.setName(name);			
			SchedulerService sservice = schedulerServiceContainer.getSchedulerService(name);
			if (sservice.isInitialized()) {
				SchedulerMetaData smeta = sservice.getSchedulerMetaData();
				sstatus.setInitialized(sservice.isInitialized());
				sstatus.setPaused(sservice.isPaused());
				sstatus.setStarted(sservice.isStarted() && !sservice.isShutdown());
				sstatus.setSchedulerMetaData(smeta);
				sstatus.setJobCount(sservice.getJobDetails().size());
			} else {
				sstatus.setInitialized(false);
			}
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
