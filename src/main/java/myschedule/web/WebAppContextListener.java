package myschedule.web;

import java.io.InputStream;
import java.util.Properties;

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
	public static final String VIEWS_PATH = "/WEB-INF/views";
	public static final String VERSION_RES_NAME = "myschedule/version.properties";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// Adding "contextPath" variable to all JSP pages.
		ServletContext ctx = sce.getServletContext();
		String contextPath = ctx.getContextPath();
		String myscheduleVersion = getMyScheduleVersion();

		ctx.setAttribute("myscheduleVersion", myscheduleVersion);
		logger.info("Set attribute myscheduleVersion=" + ctx.getAttribute("myscheduleVersion"));
		
		ctx.setAttribute("contextPath", contextPath);
		logger.info("Set attribute contextPath=" + ctx.getAttribute("contextPath"));
		
		ctx.setAttribute("mainPath", contextPath + MAIN_PATH);
		logger.info("Set attribute actionPath=" + ctx.getAttribute("actionPath"));
		
		ctx.setAttribute("viewsPath", contextPath + VIEWS_PATH);
		logger.info("Set attribute viewsPath=" + ctx.getAttribute("viewsPath"));
		
		ctx.setAttribute("themeName", DEFAULT_THEME_NAME);
		logger.info("Set attribute themeName=" + ctx.getAttribute("themeName"));
		
		logger.info("Web application initialized.");
	}

	private String getMyScheduleVersion() {
		Properties props = new Properties();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream inStream = cl.getResourceAsStream(VERSION_RES_NAME);
		try {
			props.load(inStream);
		} catch (Exception e) {
			logger.error("Failed to load resource: " + VERSION_RES_NAME, e);
		}
		String name = props.getProperty("name", "myschedule");
		String version = props.getProperty("version", "UNKNOWN");
		return name + "-" + version;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Web application destroyed.");
	}

}
