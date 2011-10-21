package myschedule.web.servlet.app;

import myschedule.web.AppConfig;
import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.ViewDataActionHandler;
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
		DashboardHandlers dashboardHandlers = appConfig.getDashboardHandler();
		JobHandlers jobHandlers = appConfig.getJobHandlers();
		SchedulerHandlers schedulerHandlers = appConfig.getSchedulerHandlers();
		ScriptingHandlers scriptingHandlers = appConfig.getScriptingHandlers();
		
		addActionHandler("", dashboardHandlers.getIndexHandler()); // Default landing page.
		addActionHandler("/dashboard/get-config-eg", dashboardHandlers.getConfigExampleHandler());
		addActionHandler("/dashboard/create", dashboardHandlers.getCreateHandler());
		addActionHandler("/dashboard/create-action", dashboardHandlers.getCreateActionHandler());
		addActionHandler("/dashboard/list", dashboardHandlers.getListHandler());
		addActionHandler("/dashboard/modify", dashboardHandlers.getModifyHandler());
		addActionHandler("/dashboard/modify-action", dashboardHandlers.getModifyActionHandler());
		addActionHandler("/dashboard/delete-action", dashboardHandlers.getDeleteActionHandler());
		addActionHandler("/dashboard/shutdown", dashboardHandlers.getShutdownHandler());
		addActionHandler("/dashboard/init", dashboardHandlers.getInitHandler());
		addActionHandler("/dashboard/switch-scheduler", dashboardHandlers.getSwitchSchedulerHandler());
		
		addActionHandler("/job/list", jobHandlers.getListHandler());
		addActionHandler("/scheduler/detail", schedulerHandlers.getDetailHandler());
		addActionHandler("/scripting/run", scriptingHandlers.getRunHandler());
		addActionHandler("/about", new ViewDataActionHandler());
	}	
}
