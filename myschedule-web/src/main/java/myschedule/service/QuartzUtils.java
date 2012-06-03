package myschedule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import myschedule.quartz.extra.SchedulerTemplate;
import org.quartz.Calendar;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

/**
 * Utilities specific to Quartz library.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class QuartzUtils {
	
	public static boolean isSimpleTrigger(Object trigger) {
		return trigger instanceof SimpleTrigger;
	}
	
	public static boolean isCronTrigger(Object trigger) {
		return trigger instanceof CronTrigger;
	}
	
	public static boolean isCalendarIntervalTrigger(Object trigger) {
		return trigger instanceof CalendarIntervalTrigger;
	}
	
	public static boolean isDailyTimeIntervalTrigger(Object trigger) {
		return trigger instanceof DailyTimeIntervalTrigger;
	}	
		
	public static String getMisfireInstructionName(Trigger trigger) {
		int code = trigger.getMisfireInstruction();

		if (code == Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
			return "MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY(" + code + ")";
		} else if (code == Trigger.MISFIRE_INSTRUCTION_SMART_POLICY) {
			return "MISFIRE_INSTRUCTION_SMART_POLICY(" + code + ")";
		}
		
		if (trigger instanceof SimpleTrigger) {
			if (code == SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW) {
				return "MISFIRE_INSTRUCTION_FIRE_NOW(" + code + ")";
			} else if (code == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
				return "MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT(" + code + ")";
			} else if (code == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT) {
				return "MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT(" + code + ")";
			} else if (code == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT) {
				return "MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT(" + code + ")";
			} else if (code == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT) {
				return "MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT(" + code + ")";
			}
		} else if (trigger instanceof CronTrigger) {
			if (code == CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
				return "MISFIRE_INSTRUCTION_DO_NOTHING(" + code + ")";
			} else if (code == CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
				return "MISFIRE_INSTRUCTION_FIRE_ONCE_NOW(" + code + ")";
			}
		} else if (trigger instanceof CalendarIntervalTrigger) {
			if (code == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
				return "MISFIRE_INSTRUCTION_DO_NOTHING(" + code + ")";
			} else if (code == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
				return "MISFIRE_INSTRUCTION_FIRE_ONCE_NOW(" + code + ")";
			}
		} else if (trigger instanceof DailyTimeIntervalTrigger) {
			if (code == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
				return "MISFIRE_INSTRUCTION_DO_NOTHING(" + code + ")";
			} else if (code == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
				return "MISFIRE_INSTRUCTION_FIRE_ONCE_NOW(" + code + ")";
			}
		}

		return "MISFIRE_INSTRUCTION_?(" + code + ")";
	}
	
	public static boolean isTriggerPaused(Trigger trigger, SchedulerTemplate scheduler) {
		TriggerState state = scheduler.getTriggerState(trigger.getKey());
		return state == TriggerState.PAUSED;
	}
	
	/** Get a list of next fire time and a desc of whether it will be excluded by calendar or not. */
	public static List<Tuple2<Date, String>> getNextFireTimesWithExclusionDesc(
			SchedulerTemplate st, Trigger trigger, int fireTimesCount) {
		List<Tuple2<Date, String>> result = new ArrayList<Tuple2<Date, String>>();		
		List<Date> nextFireTimes = st.getNextFireTimes(trigger, new Date(), fireTimesCount);		
		// Calculate excludeByCalendar
		String calName = trigger.getCalendarName();
		if (calName != null) {
			try {
				Scheduler scheduler = st.getScheduler();
				Calendar cal = scheduler.getCalendar(calName);
				for (Date dt : nextFireTimes) {
					String desc = "No";
					if (!cal.isTimeIncluded(dt.getTime())) {
						desc = "Yes. " + calName + ": " + cal.toString();
					}
					result.add(new Tuple2<Date, String>(dt, desc));
				}
			} catch (SchedulerException e) {
				throw new ErrorCodeException(
						ErrorCode.SCHEDULER_PROBLEM, "Failed to calculate next fire times with Calendar " + calName, e);
			}
		} else {
			for (Date dt : nextFireTimes) {
				String desc = null;
				result.add(new Tuple2<Date, String>(dt, desc));
			}
		}
		return result;
	}
}
