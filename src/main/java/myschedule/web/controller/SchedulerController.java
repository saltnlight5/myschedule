package myschedule.web.controller;

import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import myschedule.service.ObjectUtils;
import myschedule.service.SchedulerServiceFinder;
import myschedule.service.ObjectUtils.Getter;
import myschedule.service.SchedulerService;

import org.quartz.SchedulerException;
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
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:summary";
	}

	@RequestMapping(value="/summary", method=RequestMethod.GET)
	public ModelMap schedulerSummary(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		SchedulerSummaryPageData data = new SchedulerSummaryPageData();
		populateSummaryPageData(data, schedulerMetaData);
		return new ModelMap("data", data);
	}

	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public ModelMap detail(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		SchedulerDetailPageData data = new SchedulerDetailPageData();
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		populateSummaryPageData(data, schedulerMetaData);
		if (!schedulerMetaData.isInStandbyMode()) {
			data.setSchedulerDetail(getSchedulerDetail(schedulerMetaData));
		}
		return new ModelMap("data", data);
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
		schedulerService.stop();
		return "redirect:detail";
	}
	
	@RequestMapping(value="/switch-scheduler-action", method=RequestMethod.POST)
	public ModelMap switchSchedulerAction(
			@RequestParam String newSchedulerName,
			HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.switchSchedulerService(newSchedulerName, session);
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		SchedulerSummaryPageData data = new SchedulerSummaryPageData();
		populateSummaryPageData(data, schedulerMetaData);
		return new ModelMap("data", data);
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

	protected void populateSummaryPageData(SchedulerSummaryPageData data, SchedulerMetaData schedulerMetaData) {
		data.setSchedulerName(schedulerMetaData.getSchedulerName());
		data.setSchedulerInStandbyMode(schedulerMetaData.isInStandbyMode());
		try {
			data.setSchedulerSummary(schedulerMetaData.getSummary());
		} catch (SchedulerException e) {
			logger.warn("Failed to get scheduler summary.", e);
			data.setSchedulerSummary("NO_DATA!");
		}
	}

}
