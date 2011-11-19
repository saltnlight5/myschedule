package myschedule.web;

import java.io.File;
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
public class AppConfig extends PropsConfig implements Initable {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	// This class singleton instance access
	// ====================================
	private static AppConfig instance;
	
	private AppConfig() {
		setConfigFileKey("myschedule.config");
		setConfigFileDefaultName("classpath:myschedule/myschedule-config.properties");
	}
	
	synchronized public static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
		}
		return instance;
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
		// Initialize properties config
		initConfig();

		serviceContainer = new ServiceContainer();		
		resourceLoader = new ResourceLoader();
		
		Class<?> configStoreCls = getConfigClass("myschedule.configStore.class", "myschedule.service.FileConfigStore");
		ConfigStore configStore = newInstance(configStoreCls);
		if (configStore instanceof FileConfigStore) {
			String myScheduleDir = System.getProperty("user.home") + "/myschedule2/configs";
			myScheduleDir = getConfig("myschedule.configStore.FileConfigStore.directory", myScheduleDir);
			File configDir = new File(myScheduleDir);
			logger.info("FileConfigStore directory set to: {}", configDir);
			
			FileConfigStore fileConfigStore = (FileConfigStore)configStore;
			fileConfigStore.setStoreDir(configDir);
		}
		logger.info("ConfigStore set to: {}", configStore);
		serviceContainer.addService(configStore);
		
		schedulerContainer = new SchedulerContainer();
		schedulerContainer.setConfigStore(configStore);
		serviceContainer.addService(schedulerContainer);
		
		dashboardHandler = new DashboardHandlers();
		dashboardHandler.setSchedulerContainer(schedulerContainer);
		dashboardHandler.setResourceLoader(resourceLoader);

		int defaultFireTimesCount = getConfigInt("myschedule.handlers.JobHandler.defaultFireTimesCount", 20);
		jobHandlers = new JobHandlers();
		jobHandlers.setDefaultFireTimesCount(defaultFireTimesCount);
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
	
	private <T> T newInstance(Class<?> cls) {
		Object obj;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		@SuppressWarnings("unchecked")
		T result = (T)obj;
		return result;
	}
	
	@Override
	public void destroy() {
		if (serviceContainer != null) {
			// Ensure all services get stop and destroy
			serviceContainer.stop();
			serviceContainer.destroy();
		}
	}
	
	@Override
	public boolean isInited() {
		throw new RuntimeException("Not supported.");
	}
	
	// Web App Config
	// ==============
	public static final String DEFAULT_THEME_NAME = "smoothness";
	public static final String DEFAULT_MAIN_SERVLET_NAME = "/main";
	public static final String DEFAULT_VIEW_DIRECTORY = "/WEB-INF/jsp/main";
	
	private static final String MY_SCHEDULE_VERSION_RES_NAME = "META-INF/maven/myschedule/myschedule-quartz-extra/pom.properties";
	private static final String QUARTZ_VERSION_RES_NAME = "META-INF/maven/org.quartz-scheduler/quartz/pom.properties";
	
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String myscheduleVersion = getMyScheduleVersion();
		String contextPath = ctx.getContextPath();
		String quartzVersion = getQuartzVersion();
		
		ctx.setAttribute("myscheduleVersion", myscheduleVersion);
		logger.info("Set webapp attribute myscheduleVersion=" + ctx.getAttribute("myscheduleVersion"));
		
		ctx.setAttribute("quartzVersion", quartzVersion);
		logger.info("Set webapp attribute quartzVersion=" + ctx.getAttribute("quartzVersion"));

		ctx.setAttribute("contextPath", contextPath);
		logger.info("Set webapp attribute contextPath=" + ctx.getAttribute("contextPath"));

		String mainServletPathName = getMainServletPathName();
		ctx.setAttribute("mainPath", contextPath + mainServletPathName);
		logger.info("Set webapp attribute mainPath=" + ctx.getAttribute("mainPath"));

		String viewsDirectory = getViewsDirectory();
		ctx.setAttribute("viewsPath", contextPath + viewsDirectory);
		logger.info("Set webapp attribute viewsPath=" + ctx.getAttribute("viewsPath"));
		
		String themeName = getConfig("myschedule.web.themeName", DEFAULT_THEME_NAME);
		ctx.setAttribute("themeName", themeName);
		logger.info("Set webapp attribute themeName=" + ctx.getAttribute("themeName"));
	}
	
	public String getMainServletPathName() {
		return getConfig("myschedule.web.mainServletPathName", DEFAULT_MAIN_SERVLET_NAME);
	}
	
	public String getViewsDirectory() {
		return getConfig("myschedule.web.viewsDirectory", DEFAULT_VIEW_DIRECTORY);
	}
		
	private String getMyScheduleVersion() {
		try {
			Properties props = resourceLoader.loadProperties(MY_SCHEDULE_VERSION_RES_NAME);
			String version = props.getProperty("version");
			return "myschedule-" + version;
		} catch (RuntimeException e) {
			logger.warn("Failed to get myschedule version properties. Use LATEST.SNAPSHOT label instead.", e);
			return "myschedule-LATEST.SNAPSHOT";
		}
	}

	private String getQuartzVersion() {
		try {
			Properties props = resourceLoader.loadProperties(QUARTZ_VERSION_RES_NAME);
			String version = props.getProperty("version");
			return "quartz-" + version;
		} catch (RuntimeException e) {
			logger.warn("Failed to get quartz version properties. Use UNKNOWN label instead.", e);
			return "quartz-UNKNOWN";
		}
	}
}
