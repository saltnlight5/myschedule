package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;

public class SimpleActionHandler implements ActionHandler {
	
	protected String overrideViewName;
	
	public SimpleActionHandler() {
	}
	
	public SimpleActionHandler(String overrideViewName) {
		this.overrideViewName = overrideViewName;
	}

	@Override
	public ViewData handle(String actionPath, HttpServletRequest req) {
		String viewName = actionPath;
		if (overrideViewName != null) {
			viewName = overrideViewName;
		}
		ViewData viewData = ViewData.view(viewName);
		processAction(actionPath, viewData);
		return viewData;
	}

	protected void processAction(String actionPath, ViewData viewData) {
	}
}
