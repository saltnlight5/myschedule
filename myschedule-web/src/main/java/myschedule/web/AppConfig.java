package myschedule.web;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import myschedule.service.FileSchedulerConfigDao;
import myschedule.service.Initable;
import myschedule.service.SchedulerConfigService;
import myschedule.service.SchedulerServiceRepository;
import myschedule.service.ServiceContainer;
import myschedule.web.servlet.app.SessionDataFilter;
import myschedule.web.servlet.app.handler.DashboardHandlers;
import myschedule.web.servlet.app.handler.JobHandlers;
import myschedule.web.servlet.app.handler.SchedulerHandlers;
import myschedule.web.servlet.app.handler.ScriptingHandlers;
import myschedule.web.session.SessionSchedulerServiceFinder;

/**
 * This is a singleton space to hold global application data.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class AppConfig implements Initable {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	// This class singleton instance access
	// ====================================
	protected static AppConfig instance;
	
	protected AppConfig() {
	}
	
	synchronized public static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
		}
		return instance;
	}
	
	// Application services
	// ================================
	@Getter
	protected SchedulerServiceRepository schedulerServiceRepo;
	@Getter
	protected FileSchedulerConfigDao schedulerConfigDao;
	@Getter
	protected SchedulerConfigService schedulerConfigService;
	@Getter
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	// Can leave out the getter
	protected ServiceContainer serviceContainer;
		
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
	
	@Getter
	protected DashboardHandlers dashboardHandler;	
	@Getter
	protected JobHandlers jobHandlers;
	@Getter
	protected SchedulerHandlers schedulerHandlers;
	@Getter
	protected ScriptingHandlers scriptingHandlers;
	@Getter
	protected SessionDataFilter sessionDataFilter;
	
	@Override
	public void init() {
		schedulerServiceRepo = SchedulerServiceRepository.getInstance();

		String myScheduleDir = System.getProperty("user.home") + "/myschedule2/configs";
		schedulerConfigDao = new FileSchedulerConfigDao();
		schedulerConfigDao.setStoreDir(new File(myScheduleDir));
		
		schedulerConfigService = new SchedulerConfigService();
		schedulerConfigService.setSchedulerConfigDao(schedulerConfigDao);
		schedulerConfigService.setSchedulerServiceRepo(schedulerServiceRepo);
		
		schedulerServiceFinder = new SessionSchedulerServiceFinder();
		schedulerServiceFinder.setSchedulerServiceRepo(schedulerServiceRepo);
		
		dashboardHandler = new DashboardHandlers();
		dashboardHandler.setSchedulerServiceFinder(schedulerServiceFinder);
		dashboardHandler.setSchedulerConfigService(schedulerConfigService);
		dashboardHandler.setSchedulerServiceRepo(schedulerServiceRepo);

		jobHandlers = new JobHandlers();
		jobHandlers.setSchedulerServiceFinder(schedulerServiceFinder);
		
		schedulerHandlers = new SchedulerHandlers();
		schedulerHandlers.setSchedulerServiceFinder(schedulerServiceFinder);
		schedulerHandlers.setSchedulerConfigService(schedulerConfigService);
		schedulerHandlers.setSchedulerServiceRepo(schedulerServiceRepo);
		
		scriptingHandlers = new ScriptingHandlers();
		scriptingHandlers.setSchedulerServiceFinder(schedulerServiceFinder);
		
		sessionDataFilter = new SessionDataFilter();
		sessionDataFilter.setSchedulerServiceFinder(schedulerServiceFinder);

		serviceContainer = new ServiceContainer();
		serviceContainer.addService(schedulerConfigDao);
		serviceContainer.addService(schedulerConfigService);

		serviceContainer.init();
		serviceContainer.start();
	}
	
	@Override
	public void destroy() {
		serviceContainer.destroy();
		serviceContainer.stop();
	}
	
	@Override
	public boolean isInited() {
		throw new RuntimeException("Not supported.");
	}
}
