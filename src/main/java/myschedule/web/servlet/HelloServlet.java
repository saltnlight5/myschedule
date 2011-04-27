package myschedule.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just a demo hello servlet.
 *
 * @author Zemian Deng
 */
public class HelloServlet extends HttpServlet {
	
	private static Logger logger = LoggerFactory.getLogger(HelloServlet.class);
	
	/** serialVersionUID - long */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		log("GET requestURI: " + req.getRequestURI());
		logger.debug("requestURI: " + req.getRequestURI());
		logger.debug("requestURL: " + req.getRequestURL());
		logger.debug("queryString: " + req.getQueryString());
		
		PrintWriter out = resp.getWriter();
		out.println("Hello World.");
	}
	
}