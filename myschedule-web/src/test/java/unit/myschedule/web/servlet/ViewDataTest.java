package unit.myschedule.web.servlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import myschedule.web.servlet.ViewData;

import org.junit.Assert;
import org.junit.Test;

public class ViewDataTest {
	@Test
	public void testConstructorsAndDataMap() throws Exception {
		ViewData vd = null;
		
		vd =new ViewData("test");
		assertThat(vd.getViewName(), is("test"));
		
		vd = new ViewData("test2", "foo", "bar");
		assertThat(vd.getViewName(), is("test2"));
		assertThat((String)vd.getDataMap().get("foo"), is("bar"));

		vd = new ViewData("test2", "foo", "bar", "foo2", 123);
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
}
