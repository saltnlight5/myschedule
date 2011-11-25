package myschedule.web.servlet.app;

import myschedule.web.AppConfig;
import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.app.filter.BreadCrumbsFilter;
import myschedule.web.servlet.app.filter.SessionDataFilter;
import myschedule.web.servlet.app.handler.DashboardHandlers;
import myschedule.web.servlet.app.handler.JobHandlers;
import myschedule.web.servlet.app.handler.SchedulerHandlers;
import myschedule.web.servlet.app.handler.ScriptingHandlers;

/**
 * The main servlet that maps all action handlers for the webapp.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class MainServlet extends ActionHandlerServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		AppConfig appConfig = AppConfig.getInstance();
		
		String servletPathName = appConfig.getMainServletPathName();
		setServletPathName(servletPathName);
		
		String viewsDirectory = appConfig.getViewsDirectory();
		setViewFileNamePrefix(viewsDirectory);
		
		UrlRequestActionHandler viewNameHanler = new UrlRequestActionHandler();
		DashboardHandlers dashboardHandlers = appConfig.getDashboardHandler();
		JobHandlers jobHandlers = appConfig.getJobHandlers();
		SchedulerHandlers schedulerHandlers = appConfig.getSchedulerHandlers();
		ScriptingHandlers scriptingHandlers = appConfig.getScriptingHandlers();
		
		addActionHandler("/", viewNameHanler);      // Default fall-back handler if none matched.
		addActionHandler("/index", viewNameHanler); // Home page.
		
		addActionHandler("/dashboard/index", viewNameHanler);
		addActionHandler("/dashboard/landing", dashboardHandlers.getLandingHandler());
		addActionHandler("/dashboard/ajax/list", dashboardHandlers.getListHandler());
		addActionHandler("/dashboard/ajax/create", dashboardHandlers.getCreateHandler());
		addActionHandler("/dashboard/ajax/create-config-sample", dashboardHandlers.getConfigExampleHandler());
		addActionHandler("/dashboard/ajax/modify", dashboardHandlers.getModifyHandler());
		addActionHandler("/dashboard/create-action", dashboardHandlers.getCreateActionHandler());
		addActionHandler("/dashboard/modify-action", dashboardHandlers.getModifyActionHandler());
		addActionHandler("/dashboard/delete-action", dashboardHandlers.getDeleteActionHandler());
		addActionHandler("/dashboard/shutdown", dashboardHandlers.getShutdownHandler());
		addActionHandler("/dashboard/init", dashboardHandlers.getInitHandler());
		addActionHandler("/dashboard/switch-scheduler", dashboardHandlers.getSwitchSchedulerHandler());

		addActionHandler("/job/index", viewNameHanler);
		addActionHandler("/job/ajax/list-trigger-jobs", jobHandlers.getListTriggerJobsHandler());
		addActionHandler("/job/ajax/list-calendars", jobHandlers.getListCalendarsHandler());
		addActionHandler("/job/ajax/list-executing-jobs", jobHandlers.getListExecutingJobsHandler());
		addActionHandler("/job/ajax/list-no-trigger-jobs", jobHandlers.getListNoTriggerJobsHandler());
		addActionHandler("/job/ajax/job-detail", jobHandlers.getJobDetailHandler());
		addActionHandler("/job/ajax/load-xml", jobHandlers.getLoadXmlHandler());
		addActionHandler("/job/ajax/trigger-detail", jobHandlers.getTriggerDetailHandler());
		addActionHandler("/job/ajax/load-xml-action", jobHandlers.getLoadXmlActionHandler());
		addActionHandler("/job/run-it-now", jobHandlers.getRunJobHandler());
		addActionHandler("/job/delete", jobHandlers.getDeleteHandler());
		addActionHandler("/job/unschedule", jobHandlers.getUnscheduleHandler());
		addActionHandler("/job/pauseTrigger", jobHandlers.getPauseTriggerHandler());
		addActionHandler("/job/resumeTrigger", jobHandlers.getResumeTriggerHandler());

		addActionHandler("/scheduler/index", viewNameHanler);
		addActionHandler("/scheduler/ajax/summary", schedulerHandlers.getSummaryHandler());
		addActionHandler("/scheduler/ajax/detail", schedulerHandlers.getDetailHandler());
		addActionHandler("/scheduler/ajax/listeners", schedulerHandlers.getListenersHandler());
		addActionHandler("/scheduler/ajax/modify", schedulerHandlers.getModifyHandler());
		addActionHandler("/scheduler/modify-action", schedulerHandlers.getModifyActionHandler());
		addActionHandler("/scheduler/pause-all-triggers", schedulerHandlers.getPauseAllTriggersHandler());
		addActionHandler("/scheduler/resume-all-triggers", schedulerHandlers.getResumeAllTriggersHandler());
		addActionHandler("/scheduler/standby", schedulerHandlers.getStandbyHandler());
		addActionHandler("/scheduler/start", schedulerHandlers.getStartHandler());
		
		addActionHandler("/scripting/ajax/run", scriptingHandlers.getRunHandler());
		addActionHandler("/scripting/ajax/run-script-sample", scriptingHandlers.getScriptExampleHandler());
		addActionHandler("/scripting/run-action", scriptingHandlers.getRunActionHandler());

        SessionDataFilter sessionDataFilter = appConfig.getSessionDataFilter();
        BreadCrumbsFilter breadCrumbsFilter = appConfig.getBreadCrumbsFilter();

        addActionFilter("/", breadCrumbsFilter); // Match to all urls
        addActionFilter("/job", sessionDataFilter);
        addActionFilter("/scheduler", sessionDataFilter);
        addActionFilter("/scripting", sessionDataFilter);
        addActionFilter("/dashboard/switch-scheduler", sessionDataFilter);
	}	
}
