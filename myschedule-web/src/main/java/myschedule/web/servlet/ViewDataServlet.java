package myschedule.web.servlet;

import java.io.IOException;
import java.util.Map;
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
abstract public class ViewDataServlet extends HttpServlet {

	private static final String JSP_NAME_SUFFIX = ".jsp";

	private static final String JSP_NAME_PREFIX = "/WEB-INF/jsp/";

	private static final String REDIRECT_PREFIX = "redirect:";

	private static final long serialVersionUID = 1L;

	private static final String HTTP_SERVLET_RESPONSE_KEY = "InternalUse.HttpServletResponse";
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.debug("Process request: {}", req.getRequestURI());
		req.setAttribute(HTTP_SERVLET_RESPONSE_KEY, resp);
		ViewData viewData = processRequest(req);
		processViewData(viewData, req, resp);
	}

	protected void processViewData(ViewData viewData, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String viewName = viewData.getViewName();
		logger.debug("View name: {}", viewName);
		if (viewName != null && viewName.length() > 0) {
			if (viewName.startsWith(REDIRECT_PREFIX)) {
				String redirectName = viewName.substring(REDIRECT_PREFIX.length());
				logger.debug("Redirect: {}", redirectName);
				resp.sendRedirect(redirectName);
			}
			String jspName = JSP_NAME_PREFIX + viewName + JSP_NAME_SUFFIX;
			logger.debug("Forward: {}", jspName);
			transferViewDataToRequestAttr(viewData, req);
			req.getRequestDispatcher(jspName).forward(req, resp);
		}
	}

	protected void transferViewDataToRequestAttr(ViewData viewData, HttpServletRequest req) {
		Map<String, Object> dataMap = viewData.getDataMap();
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			logger.trace("Adding request attr: {} = {}", entry.getKey(), entry.getValue());
			req.setAttribute(entry.getKey(), entry.getValue());
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
