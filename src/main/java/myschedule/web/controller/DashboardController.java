package myschedule.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.SchedulerConfigService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.quartz.SchedulerTemplate;
import myschedule.web.SessionSchedulerServiceFinder;
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
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Autowired @Qualifier("schedulerConfigService")
	protected SchedulerConfigService schedulerConfigService;
	
	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
		
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public DataModelMap list() {
		SchedulerStatusListPageData data = new SchedulerStatusListPageData();
		List<SchedulerStatus> names = getSchedulerStatusList();
		data.setSchedulerStatusList(names);
		return new DataModelMap(data);
	}

	@RequestMapping(value="/switch-scheduler", method=RequestMethod.GET)
	public String switchScheduler(
			@RequestParam String name,
			HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceFinder.find(session);
		String mainPath = WebAppContextListener.MAIN_PATH;
		if (schedulerService.isInited())
			return "redirect:" + mainPath + "/scheduler/summary";
		else
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
		SchedulerService<?> schedulerService = schedulerConfigService.createSchedulerService(configPropsText);
		logger.info("New schedulerService {} has been saved.", schedulerService.getServiceName());
		return new DataModelMap("schedulerService", schedulerService);
	}
	
	@RequestMapping(value="/modify-get-names", method=RequestMethod.GET)
	public DataModelMap modifyGetNames(HttpSession session) {
		List<String> schedulerNames = new ArrayList<String>(schedulerConfigService.getSchedulerServiceNames());
		Collections.sort(schedulerNames);
		return new DataModelMap("schedulerNames", schedulerNames);
	}

	@RequestMapping(value="/delete-get-names", method=RequestMethod.GET)
	public DataModelMap deleteGetNames() {
		List<String> schedulerNames = new ArrayList<String>(schedulerConfigService.getSchedulerServiceNames());
		Collections.sort(schedulerNames);
		return new DataModelMap("schedulerNames", schedulerNames);
	}
	
	@RequestMapping(value="/delete-action", method=RequestMethod.POST)
	public DataModelMap deleteAction(
			@RequestParam String name,
			HttpSession session) {

		schedulerConfigService.removeSchedulerService(name);
		session.removeAttribute(SessionSchedulerServiceFinder.SESSION_DATA_KEY);
		
		// save data for view page
		return new DataModelMap("removedSchedulerName", name);
	}
	
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String init(@RequestParam String name) {
		SchedulerService<?> schedulerService = schedulerServiceRepo.getSchedulerService(name);
		schedulerService.init();
		return "redirect:list";
	}
	
	@RequestMapping(value="/shutdown", method=RequestMethod.GET)
	public String shutdown(@RequestParam String name) {
		SchedulerService<?> schedulerService = schedulerServiceRepo.getSchedulerService(name);
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
		List<String> names = schedulerServiceRepo.getSchedulerServiceNames();
		for (String name : names) {
			SchedulerStatus sstatus = new SchedulerStatus();
			sstatus.setName(name);			
			QuartzSchedulerService schedulerService =(QuartzSchedulerService)schedulerServiceRepo.getSchedulerService(name);
			SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerService.getScheduler());
			if (schedulerService.isInited() && !schedulerTemplate.isShutdown()) {
				SchedulerMetaData smeta = schedulerTemplate.getSchedulerMetaData();
				sstatus.setInitialized(schedulerService.isInited());
				sstatus.setPaused(schedulerService.isStarted() && schedulerTemplate.isInStandbyMode());
				sstatus.setStarted(schedulerService.isStarted());
				sstatus.setShutdown(schedulerTemplate.isShutdown());
				sstatus.setStandby(schedulerTemplate.isInStandbyMode());
				sstatus.setSchedulerMetaData(smeta);
				sstatus.setJobCount(schedulerTemplate.getJobDetails().size());
			} else {
				sstatus.setInitialized(false);
			}
			result.add(sstatus);
		}
		return result;
	}
}
