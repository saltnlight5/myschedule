package myschedule.service;

import java.util.Collection;

public interface SchedulerConfigDao {
	
	void save(SchedulerConfig sc);
	
	void update(SchedulerConfig sc);
	
	SchedulerConfig load(String configId);
	
	SchedulerConfig delete(String configId);
	
	Collection<String> getAllSchedulerConfigIds();

}
