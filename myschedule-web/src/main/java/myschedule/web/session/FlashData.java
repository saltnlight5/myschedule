package myschedule.web.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage of flash data. These will be reset when you getFlashData least once.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class FlashData {

	public static final String FLASH_DATA_KEY = "flashData";

	protected Map<String, Serializable> dataMap = null;
	protected int dataMapGetCount;
	
	public void addData(String key, Serializable value) {
		if (dataMap ==  null || dataMapGetCount > 0) {
			dataMap = new HashMap<String, Serializable>();
			dataMapGetCount = 0;
		}
		dataMap.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getData(String key) {
		dataMapGetCount ++;
		return (T)dataMap.get(key);
	}
		
	@Override
	public String toString() {
		return "FlashData[dataMap.size=" + dataMap.size() + ", dataMapGetCount=" + dataMapGetCount + "]";
	}
}
