package myschedule.service;

import java.util.Set;

/**
 * A service to support config CRUD operations.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public interface ConfigStore {
	
	String create(String config);
	
	String get(String id);
	
	void delete(String id);

	Set<String> getAllIds();
	
	void update(String id, String config);
}

