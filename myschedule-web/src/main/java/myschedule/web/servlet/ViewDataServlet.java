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
 * A base Servlet class that processes general Http request and responses. This class will allow subclass to process 
 * the request and return a {@link ViewData} object. Then this class will take the ViewData.viewName and do a either 
 * forward to render the view. 
 * 
 * <p>
 * If {@link ViewData} is 'null' value it assume it has handled it's own response output, and this class will not do 
 * further processing.
 * 
 * <p>
 * The default impl is to use JSP as view, and it will automatically resolve JSP file in this format:
 * <code>JPS_FILE = JSP_NAME_PREFIX + <servletPath> + <ViewData.viewName> + JSP_NAME_SUFFIX</code>.
 * Eg: <code>/WEB-INF/jsp/demo/test.jsp</code>
 * 
 * <p>
 * If subclass return ViewData.viewName with 'redirect:' prefix, then it will be send as redirect without 
 * rendering the view. 
 *  
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
abstract public class ViewDataServlet extends HttpServlet {

	private static final String JSP_NAME_PREFIX = "/WEB-INF/jsp";

	private static final String JSP_NAME_SUFFIX = ".jsp";

	private static final String REDIRECT_PREFIX = "redirect:";

	private static final long serialVersionUID = 1L;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected String jspNamePrefix = JSP_NAME_PREFIX;
	
	protected String jspNameSuffix = JSP_NAME_SUFFIX;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		logger.debug("Process request: {}, queryString={}", requestURI, req.getQueryString());
		try {
			ViewData viewData = process(req, resp);
			processViewData(viewData, req, resp);
		} catch (Exception e) {
			if (e instanceof ServletException) {
				throw (ServletException)e;
			}
			throw new ServletException("Failed to process request: " + requestURI, e);
		}
	}

	protected void processViewData(ViewData viewData, HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		if (viewData == null) {
			return;
		}
		String viewName = viewData.getViewName();
		logger.debug("View name: {}", viewName);
		if (viewName == null || viewName.equals("")) {
			return;
		}
		
		// Process viewName.
		if (viewName.startsWith(REDIRECT_PREFIX)) {
			String ctxPath = req.getContextPath();
			String servletPath = req.getServletPath();
			String redirectName = ctxPath + servletPath + viewName.substring(REDIRECT_PREFIX.length());
			logger.debug("Redirect: {}", redirectName);
			resp.sendRedirect(redirectName);
			return;
		}
		String servletPath = req.getServletPath();
		String jspName = jspNamePrefix + servletPath + viewName + jspNameSuffix;
		logger.debug("Forward: {}", jspName);
		transferViewDataToRequestAttr(viewData, req);
		req.getRequestDispatcher(jspName).forward(req, resp);
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
	 * @param resp

	 * @return a view name to resolve into JSP file.
	 */
	abstract protected ViewData process(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
