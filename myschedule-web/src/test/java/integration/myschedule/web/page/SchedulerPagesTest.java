package integration.myschedule.web.page;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SchedulerPagesTest {
	private TestConfig testConfig = TestConfig.getInstance();
	@Test
	public void testDashboardListPage() throws Exception {
		WebClient webClient = new WebClient();
		try {
		    HtmlPage page = webClient.getPage(testConfig.getWebappUrl());
		    assertThat(page.getTitleText(), is("MySchedule"));
	
		    String pageAsXml = page.asXml();
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
}
