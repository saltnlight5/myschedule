package integration.myschedule.web.page;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class StaticPagesTest {
	private TestConfig testConfig = TestConfig.getInstance();
	
	@Test
	public void testDashboardListPage() throws Exception {
		WebClient webClient = new WebClient();
		try {
		    HtmlPage page = webClient.getPage(testConfig.getWebappUrl());
		    assertThat(page.getTitleText(), is("MySchedule"));
		    
		    String pageText = page.asText();
		    assertThat(pageText, containsString("This web application is powered by the following technologies and libraries."));
		    assertThat(pageText, containsString("Java"));
		    
		    HtmlElement productInfo = page.getHtmlElementById("productInfo");
		    assertThat(productInfo.asXml(), containsString("<a href=\"/myschedule/main/about\">"));
		    assertThat(productInfo.asXml(), containsString("myschedule-"));
		    assertThat(productInfo.asXml(), containsString("- Managing Quartz Schedulers with Ease."));
		} finally {
			webClient.closeAllWindows();
		}
	}
}
