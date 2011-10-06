package myschedule.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.ExceptionHolder;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.SchedulerConfigService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceRepository;
import myschedule.web.SessionSchedulerServiceFinder;
import myschedule.web.WebAppContextListener;
import myschedule.web.controller.SchedulerStatusListPageData.SchedulerStatus;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Autowired @Qualifier("schedulerConfigService")
	protected SchedulerConfigService schedulerConfigService;
	
	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
	
	@RequestMapping(value="/modify", method=RequestMethod.GET)
	public DataModelMap modify(
			@RequestParam String configId, 
			HttpSession session) {
		DataModelMap data = new DataModelMap();
		String configPropsText = schedulerConfigService.getSchedulerConfigPropsText(configId);
		data.addData("configPropsText", configPropsText);
		data.addData("configId", configId);
		return data; 
	}
	
	@RequestMapping(value="/modify-action", method=RequestMethod.POST)
	public DataModelMap modifyAction(
			@RequestParam String configId,
			@RequestParam String configPropsText,
			HttpSession session) {
		DataModelMap data = new DataModelMap("configId", configId);
		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		schedulerConfigService.modifySchedulerService(configId, configPropsText);
		if (ss.isInited()) {
			String schedulerName = new SchedulerTemplate(ss.getScheduler()).getSchedulerName();
			data.addData("schedulerName", schedulerName);
		} else {
			String schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
			data.addData("schedulerName", schedulerName);
		}
		data.addData("schedulerService", ss);
		return data;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public DataModelMap list() {
		SchedulerStatusListPageData data = new SchedulerStatusListPageData();
		List<SchedulerStatus> schedulerStatusList = getSchedulerStatusList();
		data.setSchedulerStatusList(schedulerStatusList);
		return new DataModelMap(data);
	}

	@RequestMapping(value="/switch-scheduler", method=RequestMethod.GET)
	public String switchScheduler(
			@RequestParam String configId,
			HttpSession session) {
		schedulerServiceFinder.switchSchedulerService(configId, session);
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		String mainPath = WebAppContextListener.MAIN_PATH;
		if (schedulerService.isInited()) {
			return "redirect:" + mainPath + "/job/list";
		} else {
			return "redirect:" + mainPath + "/scheduler/summary";
		}
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public DataModelMap create() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="/create-action", method=RequestMethod.POST)
	public DataModelMap createAction(
			@RequestParam String configPropsText,
			HttpSession session) {
		ExceptionHolder eh = new ExceptionHolder();
		SchedulerService<?> ss = schedulerConfigService.createSchedulerService(configPropsText, eh);
		if (eh.hasException()) {
			DataModelMap data = new DataModelMap("schedulerService", ss);
			data.addData("initFailedExceptionString", ExceptionUtils.getFullStackTrace(eh.getException()));
			return data;
		} else {
			QuartzSchedulerService schedulerService = (QuartzSchedulerService)ss;
			SchedulerTemplate st = new SchedulerTemplate(schedulerService.getScheduler());
			logger.info("New scheduler service configId {} has been saved. The scheduler name is {}", 
					schedulerService.getSchedulerConfig().getConfigId(), st.getSchedulerName());
			return new DataModelMap("schedulerService", schedulerService);
		}
	}
	
	@RequestMapping(value="/modify-get-names", method=RequestMethod.GET)
	public DataModelMap modifyGetNames(HttpSession session) {
		return getSchedulerNamesDataModelMap();
	}
	
	protected DataModelMap getSchedulerNamesDataModelMap() {
		List<String> configIds = schedulerConfigService.getSchedulerServiceConfigIds();
		logger.debug("There are {} configIds.", configIds.size());
		Map<String, String> schedulerNamesMap = new HashMap<String, String>();
		for (String configId : configIds) {
			QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
			String name = null;
			if (ss.isInited()) {
				SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
				name = st.getSchedulerName();
			} else {
				name = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
			}
			schedulerNamesMap.put(configId, name);
			logger.debug("Mapping configId {} to schedulerName {}.", configId, name);
		}
		DataModelMap data = new DataModelMap("schedulerNamesMap", schedulerNamesMap);
		return data;
	}

	@RequestMapping(value="/delete-get-names", method=RequestMethod.GET)
	public DataModelMap deleteGetNames() {
		return getSchedulerNamesDataModelMap();
	}
	
	@RequestMapping(value="/delete-action", method=RequestMethod.GET)
	public DataModelMap deleteAction(
			@RequestParam String configId,
			HttpSession session) {
		DataModelMap data = new DataModelMap("configId", configId);
		
		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		// We might be deleting a scheduler service that hasn't been initialized yet. so scheduler name 
		// might not be available.
		if (ss.isInited()) {
			String schedulerName = new SchedulerTemplate(ss.getScheduler()).getSchedulerName();
			data.addData("schedulerName", schedulerName);
		} else {
			String schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
			data.addData("schedulerName", schedulerName);
		}
		// Now remove scheduler service.
		schedulerConfigService.removeSchedulerService(configId);
		session.removeAttribute(SessionSchedulerServiceFinder.SESSION_DATA_KEY);
		logger.info("Removed scheduler configId {} and from session data.", configId);
		
		return data;
	}
	
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String init(@RequestParam String configId) {
		QuartzSchedulerService schedulerService = schedulerServiceRepo.getQuartzSchedulerService(configId);
		schedulerService.init();
		schedulerService.start(); // This is myschedule auto start if possible feature, not direct Quartz start. So safe to call.
		return "redirect:list";
	}
	
	@RequestMapping(value="/shutdown", method=RequestMethod.GET)
	public String shutdown(@RequestParam String configId) {
		QuartzSchedulerService schedulerService = schedulerServiceRepo.getQuartzSchedulerService(configId);
		schedulerService.destroy();
		return "redirect:list";
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
		List<String> configIds = schedulerServiceRepo.getSchedulerServiceConfigIds();
		for (String configId : configIds) {
			QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
			SchedulerStatus sstatus = new SchedulerStatus();
			sstatus.setConfigId(configId);			
			if (ss.isInited()) {
				sstatus.setInitialized(true);
				try {
					SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
					SchedulerMetaData smeta = st.getSchedulerMetaData();
					sstatus.setSchedulerMetaData(smeta);
					sstatus.setName(st.getSchedulerName());
					sstatus.setStarted(ss.isStarted());
					sstatus.setShutdown(st.isShutdown());
					sstatus.setStandby(st.isInStandbyMode());
					sstatus.setJobCount(st.getAllJobDetails().size());
				} catch (RuntimeException e) {
					logger.error("Failed to get scheduler information for configId {}.", e, configId);
					
					// We need to ensure scheduler status list return without exception! So just fill bogus data except the name.
					String schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
					sstatus.setName(schedulerName);
					sstatus.setStarted(false);
					sstatus.setShutdown(false);
					sstatus.setStandby(false);
					sstatus.setJobCount(-1);
					
					// Give little more reasoning as what's going on, and append to the name.
					sstatus.setName(sstatus.getName() + " - ERROR: " + e.getMessage());
				}
			} else {
				sstatus.setInitialized(false);
				String schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
				sstatus.setName(schedulerName);
			}
			result.add(sstatus);
		}
		return result;
	}
}
