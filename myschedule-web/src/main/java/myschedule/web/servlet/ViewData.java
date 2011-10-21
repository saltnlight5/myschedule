package myschedule.web.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * A holder for a viewName and dataMap that are used to render view.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class ViewData {
	protected String viewName;
	protected Map<String, Object> dataMap = new HashMap<String, Object>();
	
	public ViewData(String viewName) {
		validateViewName(viewName);
		this.viewName = viewName;
	}
	
	public ViewData(String viewName, Object ... dataArray) {
		validateViewName(viewName);
		this.viewName = viewName;
		addData(dataArray);
	}
	
	public ViewData(String viewName, Map<String, Object> dataMap) {
		validateViewName(viewName);
		this.viewName = viewName;
		this.dataMap.putAll(dataMap);
	}
	
	public ViewData addData(Object ... dataArray) {
		if (dataArray.length % 2 != 0) {
			throw new IllegalArgumentException("Data must come in pair: key and value.");
		}
		
		for (int i = 0; i < dataArray.length; i++) {
			Object keyObj = dataArray[i];
			if (!(keyObj instanceof String)) {
				throw new IllegalArgumentException("Key must be a String type, but got: " + keyObj.getClass());
			}
			String key = (String)keyObj;
			Object value = dataArray[++i];
			dataMap.put(key, value);
		}
		
		return this;
	}
	
	public ViewData addMap(Map<String, Object> dataMap) {
		this.dataMap.putAll(dataMap);
		return this;
	}
	
	public String getViewName() {
		return viewName;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public static ViewData view(String viewName) {
		return new ViewData(viewName);
	}
	public static ViewData viewData(String viewName, Object ... dataArray) {
		return new ViewData(viewName, dataArray);
	}
	public static ViewData viewData(String viewName, Map<String, Object> dataMap) {
		return new ViewData(viewName, dataMap);
	}
	
	@Override
	public String toString() {
		return "ViewData[" + viewName + ", " + dataMap + "]";
	}

	
	protected void validateViewName(String viewName) {
		if (viewName == null) {
			throw new IllegalArgumentException("View name should not be null.");
		}
	}
}
