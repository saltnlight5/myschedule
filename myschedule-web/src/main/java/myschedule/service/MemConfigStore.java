package myschedule.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A in memory ConfigStore implementation.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class MemConfigStore extends AbstractService implements ConfigStore {
	
	private Map<String, String> configTextMap = new HashMap<String, String>();
	
	protected String generateId() {
		return UUID.randomUUID().toString();
	}
		
	@Override
	public String create(String config) {
		String id = generateId();
		configTextMap.put(id, config);
		logger.info("Text {} saved.", id);
		return id;
	}
	
	@Override
	public String get(String id) {
		return configTextMap.get(id);
	}

	@Override
	public void delete(String id) {
		if (!configTextMap.containsKey(id)) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLEM, 
					"Id " + id+ " doesn't exist.");
		}
		configTextMap.remove(id);
	}
	
	@Override
	public void update(String id, String config) {
		configTextMap.put(id, config);
		logger.info("Config {} updated.", id);
	}

	@Override
	public Set<String> getAllIds() {
		Set<String> ids = configTextMap.keySet();
		return ids;
	}
}
