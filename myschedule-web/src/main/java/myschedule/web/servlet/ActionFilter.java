package myschedule.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A callback filter apply to before and after Http request and response.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public interface ActionFilter {
	/** Return null if you want to continue the action handler, else supply a custom viewData object and end 
	 * the action before the action handler is called. */
	public ViewData beforeAction(String actionPath, HttpServletRequest req, HttpServletResponse resp) throws Exception;
	/** A chance to apply changes to the viewData after action handler. */
	public void afterAction(ViewData viewData, String actionPath, 
			HttpServletRequest req, HttpServletResponse resp) throws Exception;
}