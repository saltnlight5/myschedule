package myschedule.web.controller;

import org.springframework.ui.ModelMap;

/**
 * Just a wrapper to ModelMap to provide a "data" property that's a nested ModelMap. This is done so
 * to have consistency and shorthand for controller to return page data. View should always expect
 * ${ data.XXX } to access the data.
 *
 * @author Zemian Deng
 */
public class DataModelMap extends ModelMap {
	protected static final long serialVersionUID = 1L;
	
	public DataModelMap() {
		put("data", new ModelMap());
	}
	
	/** Let any custom object to be the "data" replacement. */
	public DataModelMap(Object object) {
		put("data", object);
	}
	
	public DataModelMap(String name, Object value) {
		put("data", new ModelMap());
		addData(name, value);
	}
	
	public DataModelMap addData(String name, Object value) {
		ModelMap data = (ModelMap)get("data");
		data.put(name, value);
		return this;
	}
}
