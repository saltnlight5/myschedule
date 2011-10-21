package myschedule.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.Setter;
import myschedule.quartz.extra.QuartzRuntimeException;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.ExceptionHolder;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.SchedulerConfigService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.Utils;
import myschedule.web.WebAppContextListener;
import myschedule.web.controller.DashboardController.ListPageData.SchedulerRow;
import myschedule.web.controller.SchedulerStatusListPageData.SchedulerStatus;
import myschedule.web.session.SessionSchedulerServiceFinder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
	
	public static final String PAGE_DATA_KEY = "data";
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Autowired @Qualifier("schedulerConfigService")
	protected SchedulerConfigService schedulerConfigService;
	
	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelMap list() {
		ListPageData listPageData = new ListPageData();
		List<String> configIds = schedulerServiceRepo.getSchedulerServiceConfigIds();
		for (String configId : configIds) {
			SchedulerRow schedulerRow = new SchedulerRow();
			listPageData.getSchedulerRows().add(schedulerRow);
			
			QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
			schedulerRow.setConfigId(configId);
			schedulerRow.setInitialized(ss.isInited());
			if (ss.isInited()) {
				try {
					SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
					schedulerRow.setName(st.getSchedulerName());
					schedulerRow.setStarted(ss.isStarted());
					schedulerRow.setNumOfJobs(st.getAllJobDetails().size());
					schedulerRow.setRunningSince(st.getSchedulerMetaData().getRunningSince());
				} catch (QuartzRuntimeException e) {
					logger.error("Failed to get scheduler information for configId " + configId + 
							". Will save exception string", e);
					String schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
					schedulerRow.setName(schedulerName);
					schedulerRow.setConnExceptionExists(true);
					String exeptionStr = ExceptionUtils.getFullStackTrace(e);
					schedulerRow.setConnExceptionString(exeptionStr);
				}
			} else {
				String schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
				schedulerRow.setName(schedulerName);
			}
		}
		return new ModelMap(PAGE_DATA_KEY, listPageData);
	}
		
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String init(@RequestParam String configId, HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceRepo.getQuartzSchedulerService(configId);
		try {
			schedulerService.init();
			schedulerService.start(); // This is myschedule auto start if possible feature, not direct Quartz start. So safe to call.
		} catch (ErrorCodeException e) {
			logger.error("Failed to initialize scheduler with configId " + configId, e);
			String execeptionStr = ExceptionUtils.getFullStackTrace(e);
			Map<String, Object> map = Utils.createMap("msg", execeptionStr, "msgType", "error");
			session.setAttribute("flashMsg", map);
		}
		return "redirect:list";
	}
	
	
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
	
	@Getter
	public static class ListPageData extends PageData {
		protected List<SchedulerRow> schedulerRows = new ArrayList<SchedulerRow>();
		
		@Getter @Setter
		public static class SchedulerRow {
			protected String configId;
			protected String name;
			protected boolean initialized;
			protected boolean started;
			protected Date runningSince;
			protected int numOfJobs;
			protected boolean connExceptionExists;
			protected String connExceptionString; // If started, but failed to get information.
		}
	}
}
