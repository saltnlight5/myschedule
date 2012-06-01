package myschedule.web.servlet.app.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import myschedule.quartz.extra.QuartzRuntimeException;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.SchedulerContainer;
import myschedule.service.SchedulerService;
import myschedule.service.ServiceUtils;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.session.SessionData;

import org.quartz.JobDetail;
import org.quartz.ListenerManager;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerHandlers {
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulerHandlers.class);

	private SchedulerContainer schedulerContainer;

	public void setSchedulerContainer(SchedulerContainer schedulerContainer) {
		this.schedulerContainer = schedulerContainer;
	}
	
	public ActionHandler getListenersHandler() {
		return listenersHandler;
	}
	private ActionHandler listenersHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			Map<String, Object> map = ViewData.mkMap();
			copySchedulerStatusData(schedulerService, map);
			SchedulerTemplate scheduler = schedulerService.getScheduler();
			try {
				ListenerManager listenerManager = scheduler.getListenerManager();
				map.put("jobListeners", listenerManager.getJobListeners());
				map.put("triggerListeners", listenerManager.getTriggerListeners());
				map.put("schedulerListeners", listenerManager.getSchedulerListeners());
				viewData.addData("data", map);
			} catch (QuartzRuntimeException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "Failed to retrieve scheduler listeners.", e);
			}
		}
	};
	
	public ActionHandler getModifyHandler() {
		return modifyHandler;
	}
	private ActionHandler modifyHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String configPropsText = schedulerContainer.getSchedulerConfig(configId);
			SchedulerTemplate scheduler = schedulerService.getScheduler();
			viewData.addData("data", ViewData.mkMap(
				"configPropsText", configPropsText,
				"configId", configId,
				"isStandby", scheduler.isInStandbyMode()));
		}
	};


	public ActionHandler getModifyActionHandler() {
		return modifyActionHandler;
	}
	private ActionHandler modifyActionHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configPropsText = viewData.findData("configPropsText");
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			schedulerContainer.modifyScheduler(configId, configPropsText);

			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			if (schedulerService.isSchedulerInitialized()) {
				String schedulerName = schedulerService.getScheduler().getSchedulerNameAndId();
				viewData.addData("data", ViewData.mkMap( 
					"configId", configId,
					"schedulerService", schedulerService,
					"schedulerName", schedulerName));
			} else {
				viewData.setViewName("redirect:/dashboard/list");
			}
		}
	};
	
	public ActionHandler getSummaryHandler() {
		return summaryHandler;
	}
	private ActionHandler summaryHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			Map<String, Object> map = ViewData.mkMap();
			copySchedulerStatusData(schedulerService, map);
			try {
				String summary = schedulerService.getScheduler().getMetaData().getSummary();
				map.put("schedulerSummary", summary);
				viewData.addData("data", map);
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Unable to get scheduler summary info.", e);
			}
		}
	};

	public ActionHandler getDetailHandler() {
		return detailHandler;
	}
	private ActionHandler detailHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			Map<String, Object> map = ViewData.mkMap();
			copySchedulerStatusData(schedulerService, map);
			if (schedulerTemplate.isStarted() && !schedulerTemplate.isShutdown()) {
				map.put("jobCount", "" + schedulerTemplate.getAllJobDetails().size());
				SchedulerMetaData schedulerMetaData = schedulerTemplate.getSchedulerMetaData();
				map.put("schedulerDetailMap", getSchedulerDetail(schedulerMetaData));
			}
			viewData.addData("data", map);
		}
	};

	public ActionHandler getPauseAllTriggersHandler() {
		return pauseAllTriggersHandler;
	}
	private ActionHandler pauseAllTriggersHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			List<Trigger> nonPausedTriggers = new ArrayList<Trigger>();
			for (JobDetail jobDetail : schedulerTemplate.getAllJobDetails()) {
				List<? extends Trigger> jobTriggers = schedulerTemplate.getTriggersOfJob(jobDetail.getKey());
				for (Trigger trigger : jobTriggers) {
					TriggerKey tk = trigger.getKey();
					if (schedulerTemplate.getTriggerState(tk) != TriggerState.PAUSED) {
						nonPausedTriggers.add(trigger);
					}
				}
			}
			schedulerTemplate.pauseAll();
			logger.info("Paused {} triggers in scheduler {}.", nonPausedTriggers.size(), schedulerTemplate.getSchedulerName());
			viewData.addData("data",  ViewData.mkMap("triggers", nonPausedTriggers));
		}
	};
	
	public ActionHandler getResumeAllTriggersHandler() {
		return resumeAllTriggersHandler;
	}
	private ActionHandler resumeAllTriggersHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			List<Trigger> pausedTriggers = schedulerTemplate.getPausedTriggers();
			schedulerTemplate.resumeAllTriggers();
			logger.info("Resumed {} triggers in scheduler {}.", pausedTriggers.size(), schedulerTemplate.getSchedulerName());
			viewData.addData("data",  ViewData.mkMap("triggers", pausedTriggers));
		}
	};
	
	public ActionHandler getStartHandler() {
		return startHandler;
	}
	private ActionHandler startHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			try {
				schedulerService.getScheduler().start();
			} catch (QuartzRuntimeException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, e);
			}
			viewData.setViewName("redirect:/scheduler/detail");
		}
	};

	public ActionHandler getStandbyHandler() {
		return standbyHandler;
	}
	private ActionHandler standbyHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			try {
				schedulerService.getScheduler().standby();
			} catch (QuartzRuntimeException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, e);
			}
			viewData.setViewName("redirect:/scheduler/detail");
		}
	};
	
	private TreeMap<String, String> getSchedulerDetail(SchedulerMetaData schedulerMetaData) {
		TreeMap<String, String> schedulerInfo = new TreeMap<String, String>();
		List<ServiceUtils.Getter> getters = ServiceUtils.getGetters(schedulerMetaData);
		for (ServiceUtils.Getter getter : getters) {
			String key = getter.getPropName();
			if (key.length() >= 1) {
				if (key.equals("summary")) // skip this field.
					continue;
				key = key.substring(0, 1).toUpperCase() + key.substring(1);
				String value = ServiceUtils.getGetterStrValue(getter);
				schedulerInfo.put(key, value);
			} else {
				logger.warn("Skipped a scheduler info key length less than 1 char. Key=" + key);
			}
		}
		logger.debug("Scheduler info map has " + schedulerInfo.size() + " items.");
		return schedulerInfo;
	}
	

	private void copySchedulerStatusData(SchedulerService schedulerService, Map<String, Object> dataMap) {
		SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
		dataMap.put("schedulerName", schedulerTemplate.getSchedulerName());
		dataMap.put("isStarted", schedulerTemplate.isStarted());
		dataMap.put("isStandby", schedulerTemplate.isInStandbyMode());
		dataMap.put("isShutdown", schedulerTemplate.isShutdown());
	}
}
