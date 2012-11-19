package myschedule.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/** 
 * A servlet context listener to initialize and destroy MySchedule application instance. We also save the instance
 * into the ServletContext space with MY_SCHEDULE_INSTANCE_KEY.
 *
 * @author Zemian Deng
 */
public class MyScheduleContextListener implements ServletContextListener {
	public static final String MY_SCHEDULE_INSTANCE_KEY = "myschedule.web.MySchedule";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		MySchedule mySchedule = MySchedule.getInstance();
		mySchedule.init();
		ctx.setAttribute(MY_SCHEDULE_INSTANCE_KEY, mySchedule);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		MySchedule mySchedule = MySchedule.getInstance();
		mySchedule.destroy();
	}

}
