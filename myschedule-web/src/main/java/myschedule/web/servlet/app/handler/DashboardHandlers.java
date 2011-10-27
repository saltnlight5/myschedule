package myschedule.web.servlet.app.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
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
import myschedule.service.ServiceUtils;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import myschedule.web.servlet.app.handler.pagedata.DashboardListPageData;
import myschedule.web.servlet.app.handler.pagedata.DashboardListPageData.SchedulerRow;
import myschedule.web.session.SessionSchedulerServiceFinder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class DashboardHandlers {
		
	@Setter
	protected SchedulerServiceRepository schedulerServiceRepo;
	@Setter
	protected SchedulerConfigService schedulerConfigService;
	@Setter
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Getter
	protected ActionHandler indexHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String redirectName = null;
			List<String> names = schedulerServiceRepo.getSchedulerServiceConfigIds();
			if (names.size() == 0) {
				redirectName = "/dashboard/create";
			} else {
				try {
					// This find method will throw exception if no scheduler are available. 
					// If returned success, then we have a good scheduler service ready to be list jobs.
					HttpSession session = viewData.getRequest().getSession(true);
					schedulerServiceFinder.findSchedulerService(session);			
					redirectName = "/job/list";
				} catch (RuntimeException e) {
					// No scheduler service are initialized, so list them all
					redirectName = "/dashboard/list";
				}
			}
			viewData.setViewName("redirect:" + redirectName);
		}
	};
	
	@Getter
	protected ActionHandler configExampleHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String name = viewData.findData("name");
			logger.debug("Getting resource: {}", name);
			ClassLoader classLoader = getClassLoader();
			InputStream inStream = classLoader.getResourceAsStream("myschedule/config/examples/" + name);
			try {
				logger.info("Returning text from resource: " + name);
				Writer writer = viewData.getResponse().getWriter();
				IOUtils.copy(inStream, writer);
				inStream.close();
				writer.flush();
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
			ExceptionHolder eh = new ExceptionHolder();
			SchedulerService<?> ss = schedulerConfigService.createSchedulerService(configPropsText, eh);
			Map<String, Object> map = ViewData.mkMap();
			if (eh.hasException()) {
				map.put("schedulerService", ss);
				map.put("initFailedExceptionString", ExceptionUtils.getFullStackTrace(eh.getException()));
			} else {
				QuartzSchedulerService schedulerService = (QuartzSchedulerService)ss;
				SchedulerTemplate st = new SchedulerTemplate(schedulerService.getScheduler());
				logger.info("New scheduler service configId {} has been saved. The scheduler name is {}", 
						schedulerService.getSchedulerConfig().getConfigId(), st.getSchedulerName());
				map.put("schedulerService", schedulerService);
			}
			viewData.addData("data", map);
		}
	};
		
	@Getter
	protected ActionHandler listHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {			
			DashboardListPageData listPageData = new DashboardListPageData();
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
			viewData.addData("data", listPageData);
		}
	};

	@Getter
	protected ActionHandler modifyHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			String configPropsText = schedulerConfigService.getSchedulerConfigPropsText(configId);
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
			String schedulerName = null;
			QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
			schedulerConfigService.modifySchedulerService(configId, configPropsText);
			if (ss.isInited()) {
				schedulerName = new SchedulerTemplate(ss.getScheduler()).getSchedulerName();
			} else {
				schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
			}
			viewData.addData("data", ViewData.mkMap( 
					"schedulerName", schedulerName,
					"configId", configId));
		}
	};
		
	@Getter
	protected ActionHandler deleteActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			HttpSession session = viewData.getRequest().getSession(true);
			String configId = viewData.findData("configId");
			String schedulerName = null;
			QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
			// We might be deleting a scheduler service that hasn't been initialized yet. so scheduler name 
			// might not be available.
			if (ss.isInited()) {
				schedulerName = new SchedulerTemplate(ss.getScheduler()).getSchedulerName();
			} else {
				schedulerName = schedulerConfigService.getSchedulerNameFromConfigProps(configId);
			}
			// Now remove scheduler service.
			schedulerConfigService.removeSchedulerService(configId);
			session.removeAttribute(SessionSchedulerServiceFinder.SESSION_DATA_KEY);
			logger.info("Removed scheduler configId {} and from session data.", configId);

			viewData.addData("data", ViewData.mkMap(
					"schedulerName", schedulerName,
					"configId", configId));
		}
	};
	
	@Getter
	protected ActionHandler shutdownHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			QuartzSchedulerService schedulerService = schedulerServiceRepo.getQuartzSchedulerService(configId);
			schedulerService.destroy();
			viewData.setViewName("redirect:/dashboard/list");
		}
	};

	@Getter
	protected ActionHandler initHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String configId = viewData.findData("configId");
			QuartzSchedulerService schedulerService = schedulerServiceRepo.getQuartzSchedulerService(configId);
			try {
				schedulerService.init();
				schedulerService.start(); // This is myschedule auto start if possible feature, not direct Quartz start. So safe to call.
			} catch (ErrorCodeException e) {
				logger.error("Failed to initialize scheduler with configId " + configId, e);
				String execeptionStr = ExceptionUtils.getFullStackTrace(e);
				Map<String, Object> map = ServiceUtils.createMap("msg", execeptionStr, "msgType", "error");
				HttpSession session = viewData.getRequest().getSession(true);
				session.setAttribute("flashMsg", map);
			}
			viewData.setViewName("redirect:/dashboard/list");
		}
	};
	
	@Getter
	protected ActionHandler switchSchedulerHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			HttpSession session = viewData.getRequest().getSession(true);
			String configId = viewData.findData("configId");
			schedulerServiceFinder.switchSchedulerService(configId, session);
			QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
			if (schedulerService.isInited()) {
				viewData.setViewName("redirect:/job/list");
			} else {
				viewData.setViewName("redirect:/scheduler/summary");
			}
		}
	};
	
	protected ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}	
}
