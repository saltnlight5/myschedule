package myschedule.webtest.dashboard;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ListPageTest {	
	// Example from tutorial (changed slightly to fit our need).
	@Test
	public void testHtmlUnitHomePage() throws Exception {
		WebClient webClient = new WebClient();
		try {
		    HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
		    assertThat(page.getTitleText(), is("Welcome to HtmlUnit"));
	
		    String pageAsXml = page.asXml();
		    assertThat(pageAsXml, containsString("<body class=\"composite\">"));
	
		    final String pageAsText = page.asText();
		    assertThat(pageAsText, containsString("Support for the HTTP and HTTPS protocols"));
		} finally {
			webClient.closeAllWindows();
		}
	}
}
