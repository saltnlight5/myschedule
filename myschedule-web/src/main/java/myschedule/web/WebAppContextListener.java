package myschedule.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A servlet context listener to initialize and destroy application upon the webapp lifecycles.
 *
 * @author Zemian Deng
 */
public class WebAppContextListener implements ServletContextListener {
	
	public static final String DEFAULT_THEME_NAME = "smoothness";
	public static final String MAIN_PATH = "/main";
	public static final String VIEWS_PATH = "/WEB-INF/jsp/main";
	public static final String VERSION_RES_NAME = "myschedule/version.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(WebAppContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		logger.debug("Initializing MySchedule on server: " + ctx.getServerInfo());
		
		AppConfig appConfig = AppConfig.getInstance();
		appConfig.init();
		appConfig.contextInitialized(sce);
		logger.info("Web application initialized.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		AppConfig appConfig = AppConfig.getInstance();
		appConfig.destroy();
		logger.info("Web application destroyed.");
	}

}
