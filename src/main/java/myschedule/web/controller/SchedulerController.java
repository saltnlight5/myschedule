package myschedule.web.controller;

import java.util.List;
import java.util.TreeMap;

import javax.annotation.Resource;

import myschedule.service.ObjectUtils;
import myschedule.service.ObjectUtils.Getter;
import myschedule.service.SchedulerService;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
	private static Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	
	@Resource
	private SchedulerService schedulerService;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:summary";
	}

	@RequestMapping(value="/summary", method=RequestMethod.GET)
	public ModelMap schedulerSummary() {
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		SchedulerSummaryPageData data = new SchedulerSummaryPageData();
		populateSummaryPageData(data, schedulerMetaData);
		return new ModelMap("data", data);
	}

	@RequestMapping(value="/detail", method=RequestMethod.GET)
	public ModelMap detail() {
		SchedulerDetailPageData data = new SchedulerDetailPageData();
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		populateSummaryPageData(data, schedulerMetaData);
		if (!schedulerMetaData.isInStandbyMode()) {
			data.setSchedulerDetail(getSchedulerDetail(schedulerMetaData));
		}
		return new ModelMap("data", data);
	}

	@RequestMapping(value="/standby", method=RequestMethod.GET)
	public String standby() {
		schedulerService.standby();
		return "redirect:detail";
	}
	
	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String start() {
		schedulerService.start();
		return "redirect:detail";
	}

	private TreeMap<String, String> getSchedulerDetail(SchedulerMetaData schedulerMetaData) {
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

	private void populateSummaryPageData(SchedulerSummaryPageData data, SchedulerMetaData schedulerMetaData) {
		data.setSchedulerName(schedulerMetaData.getSchedulerName());
		data.setSchedulerInStandbyMode(schedulerMetaData.isInStandbyMode());
		try {
			data.setSchedulerSummary(schedulerMetaData.getSummary());
		} catch (SchedulerException e) {
			logger.error("Failed to get scheduler summary.", e);
			data.setSchedulerSummary("NO_DATA!");
		}
	}

}
