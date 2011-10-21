package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple ActionHandler implementation that will hide the Http request and response objects, and let subclass
 * handle the {@link ViewData} instead. Any data added into the ViewData will be available in the View rendering.
 * 
 * <p>
 * This handler will check viewName for empty string, and if found, it will use the {@link #getDefaultViewName()} value
 * instead. It default to '/index'.
 * 
 * <p>
 * This class will auto create and populate a {@link ViewData} instance so subclass may further process it. By default,
 * it save the Http request and response so subclass may have access to it.
 * 
 * <p>
 * For simple handler that just need a view name returned without data map, then creating a instance of this class with
 * the viewName parameter is sufficient.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class ViewDataActionHandler implements ActionHandler {
	
	public static final String DEFAULT_VIEW_NAME = "/index";
	
	protected String defaultViewName = DEFAULT_VIEW_NAME;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public ViewDataActionHandler() {
	}
	
	public ViewDataActionHandler(String defaultViewName) {
		this.defaultViewName = defaultViewName;
	}
	
	public String getDefaultViewName() {
		return defaultViewName;
	}

	@Override
	public ViewData handleAction(String actionPath, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String viewName = actionPath;
		if (viewName.equals("")) {
			viewName = getDefaultViewName();
		}
		ViewData viewData = createViewData(viewName, req, resp);
		handleViewData(viewData);
		return viewData;
	}
	
	protected ViewData createViewData(String viewName, HttpServletRequest req, HttpServletResponse resp) {
		ViewData viewData = ViewData.view(viewName);
				
		// Auto save and expose these to subclass.
		viewData.setRequest(req);
		viewData.setResponse(resp);
		
		return viewData;
	}

	/** Default to empty impl. Subclass can override and handle their view data processing. */
	protected void handleViewData(ViewData viewData) {
	}
}
