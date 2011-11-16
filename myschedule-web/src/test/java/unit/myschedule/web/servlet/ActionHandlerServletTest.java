package unit.myschedule.web.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.UrlRequestActionHandler;
import org.junit.Test;

public class ActionHandlerServletTest {
	
	@Test
	public void testDefaultHandler() throws Exception {
		ServletConfig config = mock(ServletConfig.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/index.jsp")).thenReturn(dispatcher);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, res);
		main.destroy();

		verify(dispatcher).forward(req, res);
	}
	
	@Test
	public void testHelpHandler() throws Exception {
		ServletConfig config = mock(ServletConfig.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/help");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/help.jsp")).thenReturn(dispatcher);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, res);
		main.destroy();

		verify(dispatcher).forward(req, res);
	}
	
	
	@Test
	public void testHelloHandler() throws Exception {
		ServletConfig config = mock(ServletConfig.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test/hello");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test/hello.jsp")).thenReturn(dispatcher);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, res);
		main.destroy();

		verify(dispatcher).forward(req, res);
	}
	
	@Test
	public void testHello2Handler() throws Exception {
		ServletConfig config = mock(ServletConfig.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test/hello2");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test/hello2.jsp")).thenReturn(dispatcher);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, res);
		main.destroy();

		verify(dispatcher).forward(req, res);
	}

	
	@Test
	public void testTest2Handler() throws Exception {
		ServletConfig config = mock(ServletConfig.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test2");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test2.jsp")).thenReturn(dispatcher);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, res);
		main.destroy();

		verify(dispatcher).forward(req, res);
	}
	
	@Test
	public void testTest2HandlerWithSlash() throws Exception {
		ServletConfig config = mock(ServletConfig.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test2/");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test2.jsp")).thenReturn(dispatcher);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, res);
		main.destroy();

		verify(dispatcher).forward(req, res);
	}
	
	public static class MyMainServlet extends ActionHandlerServlet {
		private static final long serialVersionUID = 1L;
		@Override
		public void init() {			
			addActionHandler("/", new UrlRequestActionHandler());
			addActionHandler("/help", new UrlRequestActionHandler());
			addActionHandler("/test/hello", new UrlRequestActionHandler());
			addActionHandler("/test/hello2", new UrlRequestActionHandler());
			addActionHandler("/test2/", new UrlRequestActionHandler());
		}
	}
}
