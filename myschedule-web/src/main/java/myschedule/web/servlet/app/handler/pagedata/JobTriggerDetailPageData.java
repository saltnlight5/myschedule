package myschedule.web.servlet.app.handler.pagedata;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobTriggerDetailPageData {
	@Getter @Setter
	private int fireTimesCount;
	@Getter @Setter
	private List<Date> nextFireTimes;
	@Getter @Setter
	private JobDetail jobDetail;
	@Getter @Setter
	private List<? extends Trigger> triggers;
	@Getter @Setter
	private List<String> triggerStatusList;
	@Getter @Setter
	private boolean jobDetailShouldRecover;
	@Getter @Setter
	private List<String> excludeByCalendar;
}