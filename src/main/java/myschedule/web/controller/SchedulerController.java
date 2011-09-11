package myschedule.web.controller;

import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.SchedulerConfigService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.Utils;
import myschedule.service.Utils.Getter;
import myschedule.service.quartz.SchedulerTemplate;
import myschedule.web.SessionSchedulerServiceFinder;

import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
 * Scheduler controller.
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/scheduler")
public class SchedulerController {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Autowired @Qualifier("schedulerConfigService")
	protected SchedulerConfigService schedulerConfigService;
	
	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
	
	protected void copySchedulerStatusData(SchedulerService<?> schedulerService, DataModelMap data) {
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate((Scheduler)schedulerService.getScheduler());
		data.addData("schedulerName", schedulerTemplate.getSchedulerName());
		data.addData("isStarted", schedulerTemplate.isStarted());
		data.addData("isStandby", schedulerTemplate.isInStandbyMode());
		data.addData("isShutdown", schedulerTemplate.isShutdown());
	}
	
	@RequestMapping(value="/listeners", method=RequestMethod.GET)
	public DataModelMap listeners(HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		DataModelMap data = new DataModelMap();
		copySchedulerStatusData(schedulerService, data);
		Scheduler scheduler = schedulerService.getScheduler();
		try {
			ListenerManager listenerManager = scheduler.getListenerManager();
			data.addData("jobListeners", listenerManager.getJobListeners());
			data.addData("triggerListeners", listenerManager.getTriggerListeners());
			data.addData("schedulerListeners", listenerManager.getSchedulerListeners());
		} catch (SchedulerException e) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "Failed to retrieve scheduler listeners.", e);
		}
		return data;
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
	
	@RequestMapping(value="/summary", method=RequestMethod.GET)
	public DataModelMap summary(HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		DataModelMap data = new DataModelMap();
		copySchedulerStatusData(schedulerService, data);
		try {
			String summary = schedulerService.getScheduler().getMetaData().getSummary();
			data.addData("schedulerSummary", summary);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Unable to get scheduler summary info.", e);
		}
		return data;
	}

	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public DataModelMap detail(HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerService.getScheduler());
		DataModelMap data = new DataModelMap();
		copySchedulerStatusData(schedulerService, data);
		if (schedulerTemplate.isStarted() && !schedulerTemplate.isShutdown()) {
			data.addData("jobCount", "" + schedulerTemplate.getJobDetails().size());
			SchedulerMetaData schedulerMetaData = schedulerTemplate.getSchedulerMetaData();
			data.addData("schedulerDetailMap", getSchedulerDetail(schedulerMetaData));
		}
		return data;
	}

	@RequestMapping(value="/pauseAllTriggers", method=RequestMethod.GET)
	public String pauseAllTriggers(HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
		st.pauseAllTriggers();
		logger.info("Paused all triggers in scheduler {}.", st.getSchedulerName());
		return "redirect:detail";
	}
	
	@RequestMapping(value="/resumeAllTriggers", method=RequestMethod.GET)
	public String resumeAllTriggers(HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
		st.resumeAllTriggers();
		logger.info("Resumed all triggers in scheduler {}.", st.getSchedulerName());
		return "redirect:detail";
	}
	
	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String start(HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		try {
			schedulerService.getScheduler().start();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, e);
		}
		return "redirect:detail";
	}
	
	@RequestMapping(value="/standby", method=RequestMethod.GET)
	public String standby(HttpSession session) {
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		try {
			schedulerService.getScheduler().standby();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, e);
		}
		return "redirect:detail";
	}
	
	protected TreeMap<String, String> getSchedulerDetail(SchedulerMetaData schedulerMetaData) {
		TreeMap<String, String> schedulerInfo = new TreeMap<String, String>();
		List<Getter> getters = Utils.getGetters(schedulerMetaData);
		for (Getter getter : getters) {
			String key = getter.getPropName();
			if (key.length() >= 1) {
				if (key.equals("summary")) // skip this field.
					continue;
				key = key.substring(0, 1).toUpperCase() + key.substring(1);
				String value = Utils.getGetterStrValue(getter);
				schedulerInfo.put(key, value);
			} else {
				logger.warn("Skipped a scheduler info key length less than 1 char. Key=" + key);
			}
		}
		logger.debug("Scheduler info map has " + schedulerInfo.size() + " items.");
		return schedulerInfo;
	}
}
