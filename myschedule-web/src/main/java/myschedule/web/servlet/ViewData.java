package myschedule.web.servlet;

import java.util.HashMap;
import java.util.Map;

public class ViewData {
	public static final String VIEW_DATA_KEY = "v";
	
	protected String viewName;
	protected Map<String, Object> dataMap = new HashMap<String, Object>();
	
	public ViewData(String viewName) {
		this.viewName = viewName;
	}
	
	public ViewData(String viewName, Object ... dataArray) {
		this.viewName = viewName;
		addData(dataArray);
	}
	
	public ViewData addData(Object ... dataArray) {
		if (dataArray.length % 2 != 0) {
			throw new IllegalArgumentException("Data must come in pair: key, value.");
		}
		
		for (int i = 0; i < dataArray.length; i++) {
			String key = dataArray[i].toString();
			Object value = dataArray[++i];
			dataMap.put(key, value);
		}
		
		return this;
	}
	
	public String getViewName() {
		return viewName;
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
}
