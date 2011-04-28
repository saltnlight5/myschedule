package myschedule.web.controller;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import myschedule.service.ObjectUtils;
import myschedule.service.ObjectUtils.Getter;
import myschedule.service.SchedulerService;

import org.apache.commons.io.IOUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.InputSource;

/** HomeController
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/scheduler")
public class SchedulerController {
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	
	@Resource
	private SchedulerService schedulerService;
	
	private String sampleTriggerBeans;
	
	@RequestMapping(value="/create-trigger-form", method=RequestMethod.GET)
	public ModelMap createTriggerForm() throws Exception {
		if (sampleTriggerBeans == null) {
			String res = "/myschedule/web/controller/sample-trigger-beans.xml";
			URL url = Thread.currentThread().getContextClassLoader().getResource(res);
			sampleTriggerBeans = IOUtils.toString(url.openStream());
		}
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("sampleTriggerBeans", sampleTriggerBeans);
		return modelMap;
	}
	
	@RequestMapping(value="/create-trigger", method=RequestMethod.POST)
	public ModelMap createTrigger(
			@ModelAttribute("form") CreateTriggerForm form) throws Exception {
		
		StringReader reader = new StringReader(form.getText());
		InputSource inSource = new InputSource(reader);
		GenericApplicationContext ctx = new GenericApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);  
		xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		xmlReader.loadBeanDefinitions(inSource);
		ctx.refresh();
		
		Scheduler scheduler = schedulerService.getScheduler();
		
		// Add all simple triggers
		Map<String, SimpleTriggerBean> simpleTriggers = ctx.getBeansOfType(SimpleTriggerBean.class);
		for (Map.Entry<String, SimpleTriggerBean> entry : simpleTriggers.entrySet()) {
			logger.info("Adding SimpleTrigger beanId=" + entry.getKey());
			SimpleTriggerBean trigger = entry.getValue();
			scheduler.scheduleJob(trigger.getJobDetail(), trigger);
		}
		
		// Add all cron triggers
		Map<String, CronTriggerBean> cronTriggers = ctx.getBeansOfType(CronTriggerBean.class);
		for (Map.Entry<String, CronTriggerBean> entry : cronTriggers.entrySet()) {
			logger.info("Adding CronTrigger beanId=" + entry.getKey());
			CronTriggerBean trigger = entry.getValue();
			scheduler.scheduleJob(trigger.getJobDetail(), trigger);
		}
		
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("formSubmitStatus", simpleTriggers.size() + " triggers has been added.");
		modelMap.addAttribute("triggers", simpleTriggers);
		return modelMap;
	}
	
	public static class CreateTriggerForm {
		private String text;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
	
	@RequestMapping(value="/show", method=RequestMethod.GET)
	public ModelMap showScheduler() throws Exception {
		Scheduler scheduler = schedulerService.getScheduler();
		SchedulerMetaData schedulerMetaData = scheduler.getMetaData();
		Map<String, String> schedulerMap = new HashMap<String, String>();
		List<Getter> getters = ObjectUtils.getGetters(schedulerMetaData);
		for (Getter getter : getters)
			schedulerMap.put(getter.getPropName(), ObjectUtils.getGetterStrValue(getter));
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("schedulerMap", schedulerMap);
		return modelMap;
	}
	
	@RequestMapping(value="/list-triggers", method=RequestMethod.GET)
	public ModelMap listTriggers() {
		List<String[]> triggerNames = schedulerService.getTriggerNames();
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("triggerNames", triggerNames);
		return modelMap;
	}
	
	@RequestMapping(value="/list-scheduled-jobs", method=RequestMethod.GET)
	public ModelMap listScheduledJobs() {
		List<ScheduledJobPageData> scheduledJobs = new ArrayList<ScheduledJobPageData>();
		List<String[]> triggerNames = schedulerService.getTriggerNames();
		for (String[] pair : triggerNames) {
			ScheduledJobPageData sj = new ScheduledJobPageData();
			sj.triggerName = pair[0];
			sj.triggerGroup = pair[1];
			sj.nextFireTime = schedulerService.getNextFireTimes(sj.triggerName, sj.triggerGroup, new Date(), 1).get(0);
			scheduledJobs.add(sj);
		}
		
		// sort the data for pretty display
		Collections.sort(scheduledJobs);
		
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("scheduledJobs", scheduledJobs);
		return modelMap;
	}
	
	@RequestMapping(value="/list-firetimes/{triggerName}/{triggerGroup}/{maxCount}", method=RequestMethod.GET)
	public ModelAndView listNextFireTimes(
			@PathVariable String triggerName,
			@PathVariable String triggerGroup,
			@PathVariable int maxCount) {
		
		List<Date> fireTimes = schedulerService.getNextFireTimes(triggerName, triggerGroup, new Date(), maxCount);
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("triggerName", triggerName);
		modelMap.addAttribute("triggerGroup", triggerGroup);
		modelMap.addAttribute("maxCount", maxCount);
		modelMap.addAttribute("fireTimes", fireTimes);
		
		return new ModelAndView("scheduler/list-firetimes", modelMap);
	}
	
	public static class ScheduledJobPageData implements Comparable<ScheduledJobPageData>{
		private String triggerGroup;
		private String triggerName;
		private Date nextFireTime;
		
		/**
		 * Getter.
		 * @return the triggerGroup - String
		 */
		public String getTriggerGroup() {
			return triggerGroup;
		}		
		/**
		 * Getter.
		 * @return the triggerName - String
		 */
		public String getTriggerName() {
			return triggerName;
		}
		/**
		 * Getter.
		 * @return the nextFireTime - Date
		 */
		public Date getNextFireTime() {
			return nextFireTime;
		}
		/**
		 * Override @see java.lang.Comparable#compareTo(java.lang.Object) method.
		 * @param o
		 * @return
		 */
		@Override
		public int compareTo(ScheduledJobPageData that) {
			return triggerGroup.compareTo(that.triggerGroup) +
				triggerName.compareTo(that.triggerName);
		}
	}
}
