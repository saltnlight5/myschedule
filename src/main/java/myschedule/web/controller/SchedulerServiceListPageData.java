package myschedule.web.controller;

import java.util.List;
import java.util.Map;

import org.quartz.SchedulerMetaData;

public class SchedulerServiceListPageData {
	protected List<String> names;
	protected Map<String, SchedulerMetaData> schedulerMetaDataMap;
	
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	public Map<String, SchedulerMetaData> getSchedulerMetaDataMap() {
		return schedulerMetaDataMap;
	}
	public void setSchedulerMetaDataMap(Map<String, SchedulerMetaData> schedulerMetaDataMap) {
		this.schedulerMetaDataMap = schedulerMetaDataMap;
	}
}
