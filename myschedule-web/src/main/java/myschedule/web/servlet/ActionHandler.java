package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;

public interface ActionHandler {
	public ViewData handle(String actionPath, HttpServletRequest req);
}