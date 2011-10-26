package myschedule.webtest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DashboardTest {
	@Test
	public void testDashboardListPage() throws Exception {
		WebClient webClient = new WebClient();
		try {
		    HtmlPage page = webClient.getPage("http://localhost:8080/myschedule/main/dashboard/list");
		    assertThat(page.getTitleText(), is("MySchedule"));
	
		    String pageAsXml = page.asXml();
		    //assertThat(pageAsXml, containsString(expectedHtml("myschedule/webtest/DashboardTest-list.html")));
		    assertThat(pageAsXml, containsString("List of All Schedulers"));
		    assertThat(pageAsXml, containsString("SCHEDULER NAME"));
		    assertThat(pageAsXml, containsString("INITIALIZED"));
		    assertThat(pageAsXml, containsString("STARTED"));
		    assertThat(pageAsXml, containsString("RUNNING SINCE"));
		    assertThat(pageAsXml, containsString("# JOBS"));
		    assertThat(pageAsXml, containsString("ACTION"));
		} finally {
			webClient.closeAllWindows();
		}
	}

	protected String expectedHtml(String resName) throws IOException {
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName);
		return IOUtils.toString(inStream);
	}
}
