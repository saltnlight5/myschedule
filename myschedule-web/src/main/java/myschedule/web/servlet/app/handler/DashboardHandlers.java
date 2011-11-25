package myschedule.web.servlet.app.handler;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.session.SessionData;
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
    private ActionHandler landingHandler = new UrlRequestActionHandler() {
            @Override
            protected void handleViewData(ViewData viewData) {
                    String redirectName = null;
                    Set<String> configIds = schedulerContainer.getAllConfigIds();
                    if (configIds.size() == 0) {
                            redirectName = "/dashboard/index?selectedTab=create";
                    } else {
                            try {
                                    redirectName = "/job/index";
                            } catch (RuntimeException e) {
                                    // No scheduler service are initialized, so list them all
                                    redirectName = "/dashboard/index";
                            }
                    }
                    viewData.setViewName("redirect:" + redirectName);
            }
    };

	@Getter
	private ActionHandler listHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			List<DashboardData.SchedulerDetail> schedulerList = new ArrayList<DashboardData.SchedulerDetail>();
			for (String configId : schedulerContainer.getAllConfigIds()) {
				SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
				DashboardData.SchedulerDetail schedulerDetail = new DashboardData.SchedulerDetail();
				schedulerList.add(schedulerDetail);

				schedulerDetail.setConfigId(configId);
				schedulerDetail.setName(schedulerService.getSchedulerNameAndId());
				if (schedulerService.getInitException() != null) {
					String msg = ExceptionUtils.getRootCauseMessage(schedulerService.getInitException());
					schedulerDetail.setInitExceptionMessage(msg);
					schedulerDetail.setInitialized("N/A");
					schedulerDetail.setStarted("N/A");
					schedulerDetail.setNumOfJobs("N/A");
					schedulerDetail.setRunningSince("N/A");
				} else {				
					boolean init = schedulerService.isSchedulerInitialized();
					schedulerDetail.setInitialized("" + init);
					if (init) {					
						try {
							SchedulerTemplate scheduler = schedulerService.getScheduler();
							SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
							Date runningSince = scheduler.getSchedulerMetaData().getRunningSince();
							if (runningSince == null) {
								runningSince = new Date(); // This can happens if we come here right after a init call!
							}
							schedulerDetail.setStarted("" + scheduler.isStarted());
							schedulerDetail.setNumOfJobs("" + scheduler.getAllJobDetails().size());
							schedulerDetail.setRunningSince(df.format(runningSince));
						} catch (QuartzRuntimeException e) {
							logger.error("Failed to get scheduler information for configId " + configId, e);
							schedulerDetail.setSchedulerProblem(true);
							if (schedulerDetail.getStarted() == null) {
								schedulerDetail.setStarted("Scheduler Error");
							}
							if (schedulerDetail.getNumOfJobs() == null) {
								schedulerDetail.setNumOfJobs("Scheduler Error");
							}
							if (schedulerDetail.getRunningSince() == null) {
								schedulerDetail.setRunningSince("Scheduler Error");
							}
						}
					} else {
						schedulerDetail.setStarted("N/A");
						schedulerDetail.setNumOfJobs("N/A");
						schedulerDetail.setRunningSince("N/A");
					}
				}
			}
			viewData.addData("data", ViewData.mkMap("schedulerList", schedulerList));
		}
	};

	@Getter
	private ActionHandler configExampleHandler = new UrlRequestActionHandler() {
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
	private ActionHandler createHandler = new UrlRequestActionHandler();

	@Getter
	private ActionHandler createActionHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configPropsText = viewData.findData("configPropsText");
			// The container will auto init and start if needs to.
			schedulerContainer.createScheduler(configPropsText);
			viewData.setViewName("redirect:/dashboard/index");
		}
	};

	@Getter
	private ActionHandler modifyHandler = new UrlRequestActionHandler() {
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
	private ActionHandler modifyActionHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			String configPropsText = viewData.findData("configPropsText");
			try {
				schedulerContainer.modifyScheduler(configId, configPropsText);
			} catch (QuartzRuntimeException e) {
				logger.error("Failed to modify scheduler with configId " + configId, e);
			}
			viewData.setViewName("redirect:/dashboard/index");
		}
	};

	@Getter
	private ActionHandler deleteActionHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			try {
				schedulerContainer.deleteScheduler(configId);
			} catch (QuartzRuntimeException e) {
				logger.error("Failed to delete scheduler with configId " + configId, e);
			}
			viewData.setViewName("redirect:/dashboard/index");
		}
	};

	@Getter
	private ActionHandler initHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			try {
				schedulerService.initScheduler();
				if (schedulerService.isAutoStart()) {
					schedulerService.startScheduler();					
				}
			} catch (QuartzRuntimeException e) {
				logger.error("Failed to initialize scheduler with configId " + configId, e);
			}
			viewData.setViewName("redirect:/dashboard/index");
		}
	};

	@Getter
	private ActionHandler shutdownHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			schedulerService.shutdownScheduler();
			viewData.setViewName("redirect:/dashboard/index");
		}
	};

	@Getter
	private ActionHandler switchSchedulerHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			
			// Update session data
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			sessionData.setCurrentSchedulerConfigId(configId);
			sessionData.setCurrentSchedulerName(schedulerService.getSchedulerNameAndId());
			
			// Redirect to next view page.
			if (schedulerService.isSchedulerInitialized()) {
				viewData.setViewName("redirect:/job/index");
			} else {
				viewData.setViewName("redirect:/scheduler/index");
			}
		}
	};
}
