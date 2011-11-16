package unit.myschedule.web.servlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myschedule.web.servlet.ActionFilter;
import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.ViewData;
import org.junit.Test;

public class ActionHandlerServletTest {
	
	@Test
	public void testDefaultHandler() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/index.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testHelpHandler() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/help");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/help.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
	}
	
	
	@Test
	public void testHelloHandler() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test/hello");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test/hello.jsp")).thenReturn(dispatcher);
		
		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testHello2Handler() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test/hello2");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test/hello2.jsp")).thenReturn(dispatcher);

		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
	}

	
	@Test
	public void testTest2Handler() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test2");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test2.jsp")).thenReturn(dispatcher);

		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testTest2HandlerWithSlash() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test2/");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test2.jsp")).thenReturn(dispatcher);

		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
	}
	
	@Test
	public void testActionFilter() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getMethod()).thenReturn("GET");
		when(req.getRequestURI()).thenReturn("/main/test2/");
		when(req.getContextPath()).thenReturn("");
		when(req.getServletPath()).thenReturn("/main");
		when(req.getQueryString()).thenReturn("");
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);		
		when(req.getRequestDispatcher("/WEB-INF/jsp/main/test2.jsp")).thenReturn(dispatcher);

		ServletConfig config = mock(ServletConfig.class);
		
		MyMainServlet main = new MyMainServlet();
		main.init(config);
		main.service(req, resp);
		main.destroy();

		verify(dispatcher).forward(req, resp);
		assertThat(MyMainServlet.testFilter.beforeActionList.size(), is(1));
		assertThat(MyMainServlet.testFilter.afterActionList.size(), is(1));
	}
	
	public static class MyMainServlet extends ActionHandlerServlet {
		private static final long serialVersionUID = 1L;
		public static MyFilter testFilter = new MyFilter();
		@Override
		public void init() {			
			addActionHandler("/", new UrlRequestActionHandler());
			addActionHandler("/help", new UrlRequestActionHandler());
			addActionHandler("/test/hello", new UrlRequestActionHandler());
			addActionHandler("/test/hello2", new UrlRequestActionHandler());
			addActionHandler("/test2/", new UrlRequestActionHandler());
			
			addActionFilter("/test", testFilter);
		}
	}
	
	public static class MyFilter implements ActionFilter {
		public List<String> beforeActionList = new ArrayList<String>();
		public List<String> afterActionList = new ArrayList<String>();
		
		@Override
		public ViewData beforeAction(String actionPath, HttpServletRequest req, HttpServletResponse resp)
				throws Exception {
			beforeActionList.add(actionPath);
			return null;
		}

		@Override
		public void afterAction(ViewData viewData, String actionPath, HttpServletRequest req, HttpServletResponse resp)
				throws Exception {
			afterActionList.add(viewData.getViewName());
		}		
	}
}
