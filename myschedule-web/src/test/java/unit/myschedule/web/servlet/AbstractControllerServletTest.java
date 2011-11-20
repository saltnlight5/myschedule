package unit.myschedule.web.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myschedule.web.servlet.AbstractControllerServlet;
import myschedule.web.servlet.ViewData;
import org.junit.Test;

public class AbstractControllerServletTest {
	
	@Test
	public void testGetRequest() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/index");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/index.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("/index");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		verify(dispatcher).forward(req, resp);
	}
	
	/** POST should process same as get. */
	@Test
	public void testPostRequest() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("POST");
		when(req.getRequestURI()).thenReturn("/main/index");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/index.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("/index");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testHandlerAndActionUrl() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/myhandler/myaction");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/myhandler/myaction.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("/myhandler/myaction");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testWebappContextName() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/myhandler/myaction");
		when(req.getContextPath()).thenReturn("/webapp1");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/myhandler/myaction.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("/myhandler/myaction");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testCustomViewFilename() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/myhandler/myaction");
		when(req.getContextPath()).thenReturn("/webapp1");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher("/WEB-INF/jsp2/main/myhandler/myaction.jspx")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("/myhandler/myaction");
		main.setViewFileNamePrefix("/WEB-INF/jsp2/main");
		main.setViewFileNameSuffix(".jspx");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testRedirect() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/index");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/index.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("redirect:/somewhere");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		verify(resp).sendRedirect("/main/somewhere");
		
		verify(dispatcher, times(0)).forward(req, resp); 
	}
	
	@Test
	public void testIgnoreResponse() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/index");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/index.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyServlet main = new MyServlet("");
		main.init(config);
		main.service(req, resp);
		main.destroy();
		
		verify(resp, times(0)).sendRedirect("");
		verify(dispatcher, times(0)).forward(req, resp);
		
		// Test again with null viewName
		main = new MyServlet(null);
		main.init(config);
		main.service(req, resp);
		main.destroy();
		
		verify(resp, times(0)).sendRedirect("");
		verify(dispatcher, times(0)).forward(req, resp);
	}
	
	public static class MyServlet extends AbstractControllerServlet {
		private static final long serialVersionUID = 1L;
		private String viewName;
		public MyServlet(String viewName) {
			this.viewName = viewName;
			setServletPathName("/main");
		}
		
		@Override
		protected ViewData process(HttpServletRequest req, HttpServletResponse resp) throws Exception {
			return new ViewData(viewName);
		}
	}
}
