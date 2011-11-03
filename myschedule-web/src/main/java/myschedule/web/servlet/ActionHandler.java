package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A callback handler to process Http request and response.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public interface ActionHandler {
	public ViewData handleAction(String actionPath, HttpServletRequest req, HttpServletResponse resp) throws Exception;
}