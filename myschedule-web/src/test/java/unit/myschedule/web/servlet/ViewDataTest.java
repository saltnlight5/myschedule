package unit.myschedule.web.servlet;

import static myschedule.web.servlet.ViewData.mkMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import myschedule.web.servlet.ViewData;
import org.junit.Assert;
import org.junit.Test;

public class ViewDataTest {
	@Test
	public void testConstructorsAndDataMap() throws Exception {
		ViewData vd = null;
		
		vd =new ViewData("test");
		assertThat(vd.getViewName(), is("test"));
		
		vd = new ViewData("test2", mkMap("foo", "bar"));
		assertThat(vd.getViewName(), is("test2"));
		assertThat((String)vd.getDataMap().get("foo"), is("bar"));

		vd = new ViewData("test2", mkMap("foo", "bar", "foo2", 123));
		assertThat(vd.getViewName(), is("test2"));
		assertThat((String)vd.getDataMap().get("foo"), is("bar"));
		assertThat((Integer)vd.getDataMap().get("foo2"), is(123));
		assertThat(vd.toString(), containsString("ViewData[test2, "));
		
		vd.setViewName("test3");
		assertThat(vd.getViewName(), is("test3"));
		
		vd.addData("foo3", 77L);
		assertThat((Long)vd.getDataMap().get("foo3"), is(77L));
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("foo1", "bar1");
		vd = new ViewData("test4", dataMap);
		assertThat(vd.getViewName(), is("test4"));
		assertThat((String)vd.getDataMap().get("foo1"), is("bar1"));
		assertThat(vd.getDataMap() != dataMap, is(true)); // We should not save external map.
	}
	
	@Test
	public void testMakers() throws Exception {
		ViewData vd = null;
		
		vd = ViewData.view("test");
		assertThat(vd.getViewName(), is("test"));
		
		vd = ViewData.viewData("test2", "foo", "bar");
		assertThat(vd.getViewName(), is("test2"));
		assertThat((String)vd.getDataMap().get("foo"), is("bar"));

		vd = ViewData.viewData("test2", "foo", "bar", "foo2", 123);
		assertThat(vd.getViewName(), is("test2"));
		assertThat((String)vd.getDataMap().get("foo"), is("bar"));
		assertThat((Integer)vd.getDataMap().get("foo2"), is(123));
		assertThat(vd.toString(), containsString("ViewData[test2, "));	

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("foo1", "bar1");
		vd = ViewData.viewData("test4", dataMap);
		vd = new ViewData("test4", dataMap);
		assertThat(vd.getViewName(), is("test4"));
		assertThat((String)vd.getDataMap().get("foo1"), is("bar1"));
		assertThat(vd.getDataMap() != dataMap, is(true)); // We should not save external map.
	}
	
	@Test
	public void testBadData() throws Exception {
		try {
			ViewData.viewData("test", new Object(), new Object());
			Assert.fail("We should not allow non String key in dataMap.");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("Key must be a String type, but got: "));
		}
	}
		
	@Test
	public void testFindDataBySession() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		// Setup session data.
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("num")).thenReturn(123);
		when(session.getAttribute("letters")).thenReturn(new String[]{ "A", "B", "C" });
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getSession(false)).thenReturn(session);
		
		ViewData viewData = new ViewData("test", req, resp);
		int num = viewData.findData("num");
		assertThat(num, is(123));
		String[] letters = viewData.findData("letters");
		assertThat(letters, arrayContaining(new String[]{ "A", "B", "C" }));
	}
	
	@Test
	public void testFindDataByReqAttrs() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		// Setup session data.
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("num")).thenReturn(123);
		when(session.getAttribute("letters")).thenReturn(new String[]{ "A", "B", "C" });
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getSession(false)).thenReturn(session);
		// Setup request attributes data.
		when(req.getAttribute("num")).thenReturn(456);
		when(req.getAttribute("letters")).thenReturn(new String[]{ "D", "E", "F" });
		
		ViewData viewData = new ViewData("test", req, resp);
		int num = viewData.findData("num");
		assertThat(num, is(456));
		String[] letters = viewData.findData("letters");
		assertThat(letters, arrayContaining(new String[]{ "D", "E", "F" }));
	}
	
	@Test
	public void testFindDataByReqParams() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		// Setup session data.
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("num")).thenReturn(123);
		when(session.getAttribute("letters")).thenReturn(new String[]{ "A", "B", "C" });
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getSession(false)).thenReturn(session);
		// Setup request attributes data.
		when(req.getAttribute("num")).thenReturn(456);
		when(req.getAttribute("letters")).thenReturn(new String[]{ "D", "E", "F" });
		// Setup request parameters data.
		when(req.getParameterValues("num")).thenReturn(new String[]{ "789" });
		when(req.getParameterValues("letters")).thenReturn(new String[]{ "G", "H", "I" });
		
		ViewData viewData = new ViewData("test", req, resp);
		String num = viewData.findData("num");
		assertThat(num, is("789"));
		String[] letters = viewData.findData("letters");
		assertThat(letters, arrayContaining(new String[]{ "G", "H", "I" }));
	}

	
	@Test
	public void testFindDataByDataMap() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		// Setup session data.
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("num")).thenReturn(123);
		when(session.getAttribute("letters")).thenReturn(new String[]{ "A", "B", "C" });
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getSession(false)).thenReturn(session);
		// Setup request attributes data.
		when(req.getAttribute("num")).thenReturn(456);
		when(req.getAttribute("letters")).thenReturn(new String[]{ "D", "E", "F" });
		// Setup request parameters data.
		when(req.getParameterValues("num")).thenReturn(new String[]{ "789" });
		when(req.getParameterValues("letters")).thenReturn(new String[]{ "G", "H", "I" });
		
		ViewData viewData = new ViewData("test", req, resp);
		// Setup dataMap
		viewData.addData("num", 987, "letters", new String[]{ "J", "K", "L" });
		
		int num = viewData.findData("num");
		assertThat(num, is(987));
		String[] letters = viewData.findData("letters");
		assertThat(letters, arrayContaining(new String[]{ "J", "K", "L" }));
	}
	
	@Test
	public void testFindDataNotFound() throws Exception {
		try {
			ViewData viewData = new ViewData("test");
			viewData.addData("num", 987, "letters", new String[]{ "A", "B", "C" });
			viewData.findData("test");
			Assert.fail("We should throw exception when key not found.");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("No data found using key: "));
		}
	}
	
	@Test
	public void testFindDataWithDefaultValue() throws Exception {
		ViewData viewData = new ViewData("test");
		viewData.addData("num", 987, "letters", new String[]{ "A", "B", "C" });
		String test = viewData.findData("test", "foo");
		assertThat(test, is("foo"));
		test = viewData.findData("test2", null);
		assertThat(test, nullValue());
	}
}
