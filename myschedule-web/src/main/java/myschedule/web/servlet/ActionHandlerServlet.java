package myschedule.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Process Http requests using a map of action paths to some callback handlers. Subclass may setup this handler 
 * mappings, and concentrate on each action into smaller piece of code, instead of worry about the inner working of 
 * Http request processing.
 * 
 * <p>
 * The default action path will be parse from the Http request's URI after the 'servletPath' section. This action path
 * is use to lookup pre-registered handler instance to handle the request. A typical usage is that an application will
 * create one 'MainServlet' that subclass this Servlet, and declare it in the web.xml like this:
 * <pre>
 * {@code
 * <servlet>
 *     <servlet-name>MainServlet</servlet-name>
 *     <servlet-class>myschedule.web.servlet.app.MainServlet</servlet-class>
 *     <load-on-startup>1</load-on-startup>
 * </servlet>
 * <servlet-mapping>
 *     <servlet-name>MainServlet</servlet-name>
 *     <url-pattern>/main/*</url-pattern>
 * </servlet-mapping>
 * }
 * </pre>
 * 
 * Then this 'MainServlet' may setup handler mappings in the init() method. These action mappings are correspond to http 
 * URL on browser such as: <code>http://localhost/mywebapp/main-servlet/myaction</code>.
 *  
 * <p>An MainServlet example:
 * <pre>
 * public MyMainServlet extends ActionHandlerServlet {
 *   public void init() {
 *       addActionHandler("/", new ViewDataActionHandler()); // Eg: maps to http://localhost/mywebapp/main-servlet
 *       addActionHandler("/myaction", testAction);          // Eg: maps to http://localhost/mywebapp/main-servlet/myaction
 *   }
 *   protected ActionHandler testAction = new ViewDataActionHandler() {
 *       protected void handleViewData(ViewData viewData) {
 *         viewData.addData("message", "ServerTime=" + new java.util.Date());
 *       }            
 *   };
 * }
 * </pre>
 *  
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public abstract class ActionHandlerServlet extends AbstractControllerServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, ActionHandler> actionHandlerMappings = new HashMap<String, ActionHandler>();
	private Map<String, List<ActionFilter>> actionFilterMappings = new HashMap<String, List<ActionFilter>>();
		
	/** Allow subclass to add URL action path to a handler. This should be called in init() method of subclass. */
	protected void addActionHandler(String actionPath, ActionHandler handler) {
		actionPath = trimActionPath(actionPath);
		
		if (logger.isInfoEnabled()) {
			String ctxName = getServletContext().getContextPath();
			String fullActionPath = ctxName + getServletPathName();
			fullActionPath += actionPath;
			logger.info("Adding handler {} on action path: {}", handler, fullActionPath);
		}
		actionHandlerMappings.put(actionPath, handler);
	}
	
	protected void addActionFilter(String actionPath, ActionFilter ... filters) {
		actionPath = trimActionPath(actionPath);
		for (ActionFilter filter : filters) {
			logger.info("Adding filter {} on action path: {}", filter, actionPath);
			addActionFilterToMap(actionPath, filter);
		}
	}
	
	private void addActionFilterToMap(String actionPath, ActionFilter filter) {
		if (actionFilterMappings.containsKey(actionPath)) {
			actionFilterMappings.get(actionPath).add(filter);
			return;
		}
		List<ActionFilter> list = new ArrayList<ActionFilter>();
		list.add(filter);
		actionFilterMappings.put(actionPath, list);
	}

	/** Ensure action path does not end with '/', else remove it. */
	private String trimActionPath(String actionPath) {
		while (actionPath.endsWith("/")) {
			actionPath = actionPath.substring(0, actionPath.length() -1 );
		}
		return actionPath;
	}
	
	@Override
	protected ViewData process(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String actionPath = getActionPath(req);
		actionPath = trimActionPath(actionPath);		
		ActionHandler handler = findActionHandler(actionPath, req);
		logger.debug("Processing path {} with handler: {}", actionPath, handler);
		if (handler == null) {
			String actionServletPath = req.getServletPath()  + actionPath;
			throw new RuntimeException("Unable to find action handler for path: " + actionServletPath);
		}
		
		List<ActionFilter> filters = findActionFilters(actionPath, req);
		if (filters.size() > 0) {
			for (ActionFilter filter : filters) {
				logger.debug("Processing path {} with filter {}.", actionPath, filter);
				ViewData viewData = filter.beforeAction(actionPath, req, resp);
				if (viewData != null) {
					logger.debug("Filter has stopped the path {} processing with new viewName: .", 
							actionPath, viewData.getViewName());
					return viewData;
				}
			}
		}
		
		ViewData viewData = handler.handleAction(actionPath, req, resp);
		logger.trace("Handler result: {}", viewData);
		
		if (filters.size() > 0) {
			// Run filter in reverse order in the list for post processing.
			for (int i = filters.size() - 1; i >= 0; i--) {
				ActionFilter filter = filters.get(i);
				filter.afterAction(viewData, actionPath, req, resp);
			}
		}
		return viewData;
	}
	
	/** Extract action Path from request URI after the servletPath portion. */
	protected String getActionPath(HttpServletRequest req) {
		String contextPath = req.getContextPath();
		String servletPath = req.getServletPath();
		String reqUri = req.getRequestURI();
		return reqUri.substring(contextPath.length() + servletPath.length());
	}

	/** 
	 * Find action handler by first exact match to actionPath, else if not found then any path that match from
	 * the beginning.
	 */
	protected ActionHandler findActionHandler(String actionPath, HttpServletRequest req) {
		ActionHandler handler = actionHandlerMappings.get(actionPath);
		if (handler == null) {
			// Try to find by matching beginning of actionPath
			for (String name : actionHandlerMappings.keySet()) {
				if (actionPath.startsWith(name)) {
					handler = actionHandlerMappings.get(name);
					break;
				}
			}
		}
		return handler;
	}
	
	/** 
	 * Find all action filters by exact match to actionPath or any filter that has starts with actionPath.
	 */
	protected List<ActionFilter> findActionFilters(String actionPath, HttpServletRequest req) {
		List<ActionFilter> filters = new ArrayList<ActionFilter>();
		
		// Get exact match filters with actionPath
		if (actionFilterMappings.containsKey(actionPath)) {
			filters.addAll(actionFilterMappings.get(actionPath));
		}
		
		// Get all filters that match starts with actionPath
		for (String name : actionFilterMappings.keySet()) {
			if (actionPath.startsWith(name)) {
				filters.addAll(actionFilterMappings.get(name));
			}
		}
		
		return filters;
	}

}
