package myschedule.web;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import lombok.Getter;
import myschedule.service.ConfigStore;
import myschedule.service.FileConfigStore;
import myschedule.service.Initable;
import myschedule.service.ResourceLoader;
import myschedule.service.SchedulerContainer;
import myschedule.service.ServiceContainer;
import myschedule.web.servlet.app.filter.SessionDataFilter;
import myschedule.web.servlet.app.handler.DashboardHandlers;
import myschedule.web.servlet.app.handler.JobHandlers;
import myschedule.web.servlet.app.handler.SchedulerHandlers;
import myschedule.web.servlet.app.handler.ScriptingHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a singleton space/container to hold global application services and configuration.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class AppConfig implements Initable {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	// This class singleton instance access
	// ====================================
	private static AppConfig instance;
	
	private AppConfig() {
	}
	
	synchronized public static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
		}
		return instance;
	}
			
	// Web App Config
	// ==============
	public static final String DEFAULT_THEME_NAME = "smoothness";
	public static final String MAIN_PATH = "/main";
	public static final String VIEWS_PATH = "/WEB-INF/jsp/main";
	public static final String VERSION_RES_NAME = "myschedule/version.properties";
	
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String myscheduleVersion = getMyScheduleVersion();
		String contextPath = ctx.getContextPath();
		
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
	}

	protected String getMyScheduleVersion() {
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

	// Application services
	// ====================
	// This field is not exposed out side
	private ServiceContainer serviceContainer;
	
	@Getter
	private ConfigStore configStore;
	@Getter
	private SchedulerContainer schedulerContainer;
	@Getter
	private ResourceLoader resourceLoader;
	
	@Getter
	private DashboardHandlers dashboardHandler;	
	@Getter
	private JobHandlers jobHandlers;
	@Getter
	private SchedulerHandlers schedulerHandlers;
	@Getter
	private ScriptingHandlers scriptingHandlers;
    @Getter
    protected SessionDataFilter sessionDataFilter;
	
	@Override
	public void init() {
		serviceContainer = new ServiceContainer();
		
		resourceLoader = new ResourceLoader();
		
		String myScheduleDir = System.getProperty("user.home") + "/myschedule2/configs";
		FileConfigStore fileConfigStore = new FileConfigStore();
		fileConfigStore.setStoreDir(new File(myScheduleDir));
		serviceContainer.addService(fileConfigStore);
		configStore = fileConfigStore;
		
		schedulerContainer = new SchedulerContainer();
		schedulerContainer.setConfigStore(configStore);
		serviceContainer.addService(schedulerContainer);
		
		dashboardHandler = new DashboardHandlers();
		dashboardHandler.setSchedulerContainer(schedulerContainer);
		dashboardHandler.setResourceLoader(resourceLoader);

		jobHandlers = new JobHandlers();
		jobHandlers.setSchedulerContainer(schedulerContainer);
		
		schedulerHandlers = new SchedulerHandlers();
		schedulerHandlers.setSchedulerContainer(schedulerContainer);
		
		scriptingHandlers = new ScriptingHandlers();
		scriptingHandlers.setSchedulerContainer(schedulerContainer);
		scriptingHandlers.setResourceLoader(resourceLoader);
		
		sessionDataFilter = new SessionDataFilter();
        sessionDataFilter.setSchedulerContainer(schedulerContainer);
		
		// Ensure all services get init and started.
		serviceContainer.init();
		serviceContainer.start();
	}
	
	@Override
	public void destroy() {
		// Ensure all services get stop and destroy
		serviceContainer.stop();
		serviceContainer.destroy();
	}
	
	@Override
	public boolean isInited() {
		throw new RuntimeException("Not supported.");
	}
}
