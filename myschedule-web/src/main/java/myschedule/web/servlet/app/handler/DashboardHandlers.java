package myschedule.web.servlet.app.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import myschedule.quartz.extra.QuartzRuntimeException;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.ResourceLoader;
import myschedule.service.SchedulerContainer;
import myschedule.service.SchedulerService;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardHandlers {

	private static final Logger logger = LoggerFactory.getLogger(DashboardHandlers.class);

	@Setter
	private SchedulerContainer schedulerContainer;
	@Setter
	private ResourceLoader resourceLoader;

	@Getter
	protected ActionHandler indexHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String redirectName = null;
			Set<String> configIds = schedulerContainer.getAllConfigIds();
			if (configIds.size() == 0) {
				redirectName = "/dashboard/create";
			} else {
				try {
					SchedulerService scheduler = schedulerContainer.findFirstInitedScheduler();
					redirectName = "/job/list?configId=" + scheduler.getConfigId();
				} catch (RuntimeException e) {
					// No scheduler service are initialized, so list them all
					redirectName = "/dashboard/list";
				}
			}
			viewData.setViewName("redirect:" + redirectName);
		}
	};

	@Getter
	protected ActionHandler listHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			List<Map<String, Object>> schedulerList = new ArrayList<Map<String, Object>>();
			for (String configId : schedulerContainer.getAllConfigIds()) {
				SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
				Map<String, Object> schedulerData = new HashMap<String, Object>();
				schedulerList.add(schedulerData);

				if (schedulerService.getInitException() != null) {
					schedulerData.put("initException", schedulerService.getInitException());
				}
				schedulerData.put("configId", configId);
				if (schedulerService.isInited()) {
					schedulerData.put("inited", "true");
					try {
						SchedulerTemplate scheduler = schedulerService.getScheduler();
						schedulerData.put("name", scheduler.getSchedulerNameAndId());
						schedulerData.put("started", scheduler.isStarted());
						schedulerData.put("numOfJobs", scheduler.getAllJobDetails().size());
						schedulerData.put("runningSince", scheduler.getSchedulerMetaData().getRunningSince());
					} catch (QuartzRuntimeException e) {
						logger.error("Failed to get scheduler information for configId " + configId, e);
						schedulerData.put("name", configId);
						schedulerData.put("started", "ERROR");
						schedulerData.put("numOfJobs", "ERROR");
						schedulerData.put("runningSince", "ERROR");
						schedulerData.put("errorMsg", e.getMessage());
						schedulerData.put("errorStackTrace", ExceptionUtils.getFullStackTrace(e));
					}
				} else {
					schedulerData.put("inited", "false");
					schedulerData.put("name", configId);
					schedulerData.put("started", "N/A");
					schedulerData.put("numOfJobs", "N/A");
					schedulerData.put("runningSince", "N/A");
				}
			}
			viewData.addData("data", ViewData.mkMap("schedulerList", schedulerList));
		}
	};

	@Getter
	protected ActionHandler configExampleHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String name = viewData.findData("name");
			String resName = "myschedule/config/examples/" + name;
			try {
				Writer writer = viewData.getResponse().getWriter();
				resourceLoader.copyResource(resName, writer);
			} catch (IOException e) {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to get resource " + name, e);
			}
			// Set viewName to null, so it will not render view.
			viewData.setViewName(null);
		}
	};

	@Getter
	protected ActionHandler createHandler = new ViewDataActionHandler();

	@Getter
	protected ActionHandler createActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configPropsText = viewData.findData("configPropsText");
			String configId = schedulerContainer.createScheduler(configPropsText);
			schedulerContainer.initScheduler(configId);
			viewData.setViewName("redirect:/dashboard/list");
		}
	};

	@Getter
	protected ActionHandler modifyHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			String configPropsText = schedulerContainer.getSchedulerConfig(configId);
			logger.debug("Modifying configId={}", configId);
			viewData.addData("data", ViewData.mkMap(
					"configPropsText", configPropsText,
					"configId", configId));
		}
	};

	@Getter
	protected ActionHandler modifyActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			String configPropsText = viewData.findData("configPropsText");
			try {
				schedulerContainer.modifyScheduler(configId, configPropsText);
			} catch (QuartzRuntimeException e) {
				logger.error("Failed to modify scheduler with configId " + configId, e);
			}
			viewData.setViewName("redirect:/dashboard/list");
		}
	};

	@Getter
	protected ActionHandler deleteActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			try {
				schedulerContainer.deleteScheduler(configId);
			} catch (QuartzRuntimeException e) {
				logger.error("Failed to delete scheduler with configId " + configId, e);
			}
			viewData.setViewName("redirect:/dashboard/list");
		}
	};

	@Getter
	protected ActionHandler initHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			try {
				schedulerContainer.initScheduler(configId);
			} catch (QuartzRuntimeException e) {
				logger.error("Failed to initialize scheduler with configId " + configId, e);
			}
			viewData.setViewName("redirect:/dashboard/list");
		}
	};

	@Getter
	protected ActionHandler shutdownHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			schedulerService.stop();
			schedulerService.destroy();
			viewData.setViewName("redirect:/dashboard/list");
		}
	};

	@Getter
	protected ActionHandler switchSchedulerHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			if (schedulerService.isInited()) {
				viewData.setViewName("redirect:/job/list");
			} else {
				viewData.setViewName("redirect:/scheduler/summary");
			}
		}
	};
}
