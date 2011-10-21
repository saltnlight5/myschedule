package myschedule.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * WebAppContextListener
 *
 * @author Zemian Deng
 */
public class WebAppContextListener implements ServletContextListener {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public static final String DEFAULT_THEME_NAME = "smoothness";
	public static final String MAIN_PATH = "/main";
	public static final String VIEWS_PATH = "/WEB-INF/jsp/main";
	public static final String VERSION_RES_NAME = "myschedule/version.properties";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		logger.debug("Initializing MySchedule on server: " + ctx.getServerInfo());
		
		AppConfig appConfig = AppConfig.getInstance();
		appConfig.start();
		appConfig.contextInitialized(sce);
		logger.info("Web application initialized.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		AppConfig appConfig = AppConfig.getInstance();
		appConfig.stop();
		logger.info("Web application destroyed.");
	}

}
