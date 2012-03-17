package unit.myschedule.web.servlet;

import static org.mockito.Mockito.mock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.ViewData;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UrlRequestActionHandlerTest {
	@Test
	public void testUrlHandler() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		HttpServletRequest req = mock(HttpServletRequest.class);
		UrlRequestActionHandler handler = new UrlRequestActionHandler();
		ViewData result = handler.handleAction("/myaction", req, resp);
		assertThat(result.getViewName(), is("/myaction"));
		
		result = handler.handleAction("/myhandler/myaction", req, resp);
		assertThat(result.getViewName(), is("/myhandler/myaction"));
		
		result = handler.handleAction("", req, resp);
		assertThat(result.getViewName(), is("/index"));
		
		result = handler.handleAction("/", req, resp);
		assertThat(result.getViewName(), is("/index"));
	}
}
