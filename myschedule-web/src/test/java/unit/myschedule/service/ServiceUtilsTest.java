package unit.myschedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myschedule.service.ServiceUtils;

import org.junit.Test;

/** 
 * ObjectUtilsTest
 *
 * @author Zemian Deng
 */
public class ServiceUtilsTest {

	@Test
	public void testDump() throws Exception {
		//Utils.dump(System.getProperties());
		ServiceUtils.dump(getMapSample());
		ServiceUtils.dump(getListSample());
		ServiceUtils.dump(new String[]{});
		ServiceUtils.dump(new String[]{"a", "B"});
	}
	
	public Map<String, String> getMapSample() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "foo");
		map.put("test2", "bar");
		map.put("test3", "3");
		map.put("test4", "4");
		return map;
	}
	
	public List<String> getListSample() {
		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		return list;
	}
}
