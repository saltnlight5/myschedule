package myschedule.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class that parses request URL and delegate work to subclass method. The subclass need to return a view name 
 * for this class to load a JSP page as result.
 *  
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public abstract class ViewDataServlet extends HttpServlet {

	private static final String JSP_NAME_SUFFIX = ".jsp";

	private static final String JSP_NAME_PREFIX = "/WEB-INF/jsp/";

	private static final String REDIRECT_PREFIX = "redirect:";

	private static final long serialVersionUID = 1L;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.debug("Process request: " + req.getRequestURI());
		ViewData viewData = processRequest(req);
		String viewName = viewData.getViewName();
		logger.debug("View: " + viewName);
		if (viewName != null && viewName.length() > 0) {
			if (viewName.startsWith(REDIRECT_PREFIX)) {
				String redirectName = viewName.substring(REDIRECT_PREFIX.length());
				logger.debug("Redirect: {}", redirectName);
				resp.sendRedirect(redirectName);
			}
			String jspName = JSP_NAME_PREFIX + viewName + JSP_NAME_SUFFIX;
			logger.debug("Forward: {}", jspName);
			req.setAttribute(ViewData.VIEW_DATA_KEY, viewData.getDataMap());
			req.getRequestDispatcher(jspName).forward(req, resp);
		} else {
			logger.warn("View name is empty! Do nothing.");
		}
	}

	/**
	 * Process request and return a view name.
	 * 
	 * @param req
	 * @return a view name to be fetch the jsp page.
	 */
	protected abstract ViewData processRequest(HttpServletRequest req);
}
