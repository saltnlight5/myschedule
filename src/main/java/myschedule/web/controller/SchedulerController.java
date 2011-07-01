package myschedule.web.controller;

import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.ObjectUtils;
import myschedule.service.ObjectUtils.Getter;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceFinder;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	protected SchedulerServiceFinder schedulerServiceFinder;
	
	@RequestMapping(value="/summary", method=RequestMethod.GET)
	public DataModelMap summary(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		DataModelMap data = new DataModelMap();
		data.addData("schedulerName", schedulerService.getName());
		data.addData("isStarted", schedulerService.isStarted());
		data.addData("isPaused", schedulerService.isPaused());
		data.addData("isShutdown", schedulerService.isShutdown());
		try {
		String summary = schedulerService.getSchedulerMetaData().getSummary();
		data.addData("schedulerSummary", summary);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Unable to get scheduler summary info.", e);
		}
		return data;
	}

	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public DataModelMap detail(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		DataModelMap data = new DataModelMap();
		data.addData("schedulerName", schedulerService.getName());
		data.addData("isStarted", schedulerService.isStarted());
		data.addData("isPaused", schedulerService.isPaused());
		data.addData("isShutdown", schedulerService.isShutdown());
		if (schedulerService.isStarted() && !schedulerService.isShutdown()) {
			data.addData("jobCount", "" + schedulerService.getJobDetails().size());
			SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
			data.addData("schedulerDetailMap", getSchedulerDetail(schedulerMetaData));
		}
		return data;
	}

	@RequestMapping(value="/pause", method=RequestMethod.GET)
	public String pause(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		schedulerService.pause();
		return "redirect:detail";
	}
	
	@RequestMapping(value="/resume", method=RequestMethod.GET)
	public String resume(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		schedulerService.resume();
		return "redirect:detail";
	}
	
	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String start(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		schedulerService.start();
		return "redirect:detail";
	}
	
	@RequestMapping(value="/stop", method=RequestMethod.GET)
	public String stop(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		String name = schedulerService.getName();
		schedulerService.shutdown();
		logger.info("Scheduler service " + name + " has been shutdown.");
				
		return "redirect:summary";
	}
	
	protected TreeMap<String, String> getSchedulerDetail(SchedulerMetaData schedulerMetaData) {
		TreeMap<String, String> schedulerInfo = new TreeMap<String, String>();
		List<Getter> getters = ObjectUtils.getGetters(schedulerMetaData);
		for (Getter getter : getters) {
			String key = getter.getPropName();
			if (key.length() >= 1) {
				if (key.equals("summary")) // skip this field.
					continue;
				key = key.substring(0, 1).toUpperCase() + key.substring(1);
				String value = ObjectUtils.getGetterStrValue(getter);
				schedulerInfo.put(key, value);
			} else {
				logger.warn("Skipped a scheduler info key length less than 1 char. Key=" + key);
			}
		}
		logger.debug("Scheduler info map has " + schedulerInfo.size() + " items.");
		return schedulerInfo;
	}
}
