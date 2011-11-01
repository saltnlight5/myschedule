package myschedule.web.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A holder for a viewName and dataMap that are used to render view.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class ViewData {
	private String viewName;
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public ViewData(String viewName) {
		this.viewName = viewName;
	}
	
	public ViewData(String viewName, Object ... dataArray) {
		this.viewName = viewName;
		addData(dataArray);
	}
	
	public ViewData(String viewName, Map<String, Object> dataMap) {
		this.viewName = viewName;
		this.dataMap.putAll(dataMap);
	}
	
	public ViewData addData(Object ... dataArray) {
		dataMap.putAll(mkMap(dataArray));		
		return this;
	}
	
	public ViewData addData(Map<String, Object> dataMap) {
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
	
	/**
	 * Try to find data in dataMap and, if present, the request parameters and attribute, and session space. If
	 * not found, it will return the defVal passed in.
	 * 
	 * @param key The key to search data with.
	 * @return Found value or the defVal.
	 */
	@SuppressWarnings("unchecked")
	public <T> T findData(String key, T defVal) {
		Object result = null;
		
		// 1. Search this dataMap. 
		result = dataMap.get(key);
		if (result != null) {
			return (T)result;
		}
		
		// 2. Search the request if presents
		if (request != null) {
			String[] val = request.getParameterValues(key);
			if (val != null) {
				if (val.length == 1) {
					return (T)val[0];
				} else if (val.length > 1) {
					return (T)val;
				} else {
					// Something is not right about the Web server b/c this should not happen.
					throw new RuntimeException("We found a Http request parameter " + key + " that has zero " +
							"length array value!");
				}
			}
			
			// 3. Search request attributes
			result = request.getAttribute(key);
			if (result != null) {
				return (T)result;
			}
			
			// 4. Search request session
			HttpSession session = request.getSession(false);
			if (session != null) {
				result = session.getAttribute(key);
				if (result != null) {
					return (T)result;
				}
			}
		}
		return defVal;
	}
	
	/**
	 * Try to find data in this dataMap and, if present, the request parameters and attribute, and session space. If
	 * not found, it will throw IllegalArgumentException.
	 * 
	 * @param key The key to search data with.
	 * @return Found value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T findData(String key) {
		Object result = findData(key, null);
		if (result == null) {
			throw new IllegalArgumentException("No data found using key: " + key);
		}
		return (T)result;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "ViewData[" + viewName + ", " + dataMap + "]";
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
	
	public static Map<String, Object> mkMap(Object ... dataArray) {
		Map<String, Object> map = new HashMap<String, Object>();
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
			map.put(key, value);
		}
		return map;
	}
	
}
