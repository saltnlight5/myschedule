package myschedule.service;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

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
		
	public static String getMisfireInstructionName(Trigger trigger) {
		int code = trigger.getMisfireInstruction();

		if (code == Trigger.MISFIRE_INSTRUCTION_SMART_POLICY) {
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
		}
		
		return "MISFIRE_INSTRUCTION_?(" + code + ")";
	}
}
