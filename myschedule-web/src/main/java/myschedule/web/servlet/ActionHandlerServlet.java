package myschedule.web.servlet;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Process request with sub action path with registered handlers.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
abstract public class ActionHandlerServlet extends ViewDataServlet {

	private static final long serialVersionUID = 1L;

	protected static Map<String, ActionHandler> actionHandlerMappings = new HashMap<String, ActionHandler>();
	
	protected static void addActionHandler(String actionPath, ActionHandler handler) {
		// Ensure action path is consistent.
		if (actionPath.endsWith("/")) { 
			actionPath = actionPath.substring(0, actionPath.length() -1);
		}
		actionHandlerMappings.put(actionPath, handler);
	}
	
	@Override
	protected ViewData processRequest(HttpServletRequest req) {
		String actionPath = getActionPath(req);
		// Ensure action path is consistent.
		if (actionPath.endsWith("/")) { 
			actionPath = actionPath.substring(0, actionPath.length() -1);
		}
		logger.debug("Action path: {}", actionPath);
		
		ActionHandler handler = findActionHandler(actionPath, req);
		if (handler == null) {
			throw new RuntimeException("Unable to find action handler for path: " + actionPath);
		}
		ViewData viewData = handler.handle(actionPath, req);
		return viewData;
	}

	protected String getActionPath(HttpServletRequest req) {
		String contextPath = req.getContextPath();
		String reqUri = req.getRequestURI();
		return reqUri.substring(contextPath.length());
	}

	protected ActionHandler findActionHandler(String actionPath, HttpServletRequest req) {
		return actionHandlerMappings.get(actionPath);
	}
}
