package myschedule.web.ui;

import com.vaadin.ui.Table;
import myschedule.quartz.extra.QuartzExtraUtils;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A UI popup window to display Trigger and associated JobDetails information.
 * This window is opened from the SchedulerScreen.
 */
public class JobsWithTriggersWindow extends AbstractWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsWithTriggersWindow.class);
    private static final String DATETIME_FORMAT = "EEE yyyy-MM-dd HH:mm:ss";
    private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private MySchedule mySchedule = MySchedule.getInstance();
    private TriggerKey triggerKey;
    private JobKey jobDetailKey;

    public JobsWithTriggersWindow(MyScheduleUi myScheduleUi, String schedulerSettingsName, TriggerKey triggerKey) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        this.triggerKey = triggerKey;
        initTriggerDetailsTable();
        initJobDetailTable();
        initNexFireTimesPreviewTable();
        setCaption("Trigger and JobDetail Properties");
    }

    protected void initTriggerDetailsTable() {
        Table table = new Table("Trigger Information");
        content.addComponent(table);
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Property Name", String.class, defaultValue);
        table.addContainerProperty("Property Value", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading triggerKey={} from scheduler {}", triggerKey, schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        Trigger trigger = scheduler.getTrigger(triggerKey);
        jobDetailKey = trigger.getJobKey();
        SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
        String misfireInstructionName = getMisfireInstructionName(trigger);

        int index = 1;
        addTableItem(table, index++, "Trigger Key", "" + triggerKey);
        addTableItem(table, index++, "Description", "" + toStr(trigger.getDescription()));
        addTableItem(table, index++, "Class", "" + trigger.getClass());
        addTableItem(table, index++, "Misfire Instruction", misfireInstructionName);
        addTableItem(table, index++, "Priority", "" + trigger.getPriority());
        addTableItem(table, index++, "Calendar Name", "" + toStr(trigger.getCalendarName()));
        addTableItem(table, index++, "PreviousFireTime", toDateStr(trigger.getPreviousFireTime(), df));
        addTableItem(table, index++, "NextFireTime", toDateStr(trigger.getNextFireTime(), df));
        addTableItem(table, index++, "FinalFireTime", toDateStr(trigger.getFinalFireTime(), df));
        addTableItem(table, index++, "StartTime", toDateStr(trigger.getStartTime(), df));
        addTableItem(table, index++, "EndTime", toDateStr(trigger.getEndTime(), df));
        addTableItem(table, index++, "JobDataMap", "" + toMapStr(trigger.getJobDataMap()));
    }

    protected void initJobDetailTable() {
        Table table = new Table("JobDetail Information");
        content.addComponent(table);
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Property Name", String.class, defaultValue);
        table.addContainerProperty("Property Value", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading jobDetail={} from scheduler {}", jobDetailKey, schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        JobDetail job = scheduler.getJobDetail(jobDetailKey);
        SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);

        int index = 1;
        addTableItem(table, index++, "Job Key", "" + jobDetailKey);
        addTableItem(table, index++, "Description", "" + toStr(job.getDescription()));
        addTableItem(table, index++, "Class", "" + job.getJobClass());
        addTableItem(table, index++, "JobDataMap", "" + toMapStr(job.getJobDataMap()));
    }

    protected void initNexFireTimesPreviewTable() {
        Table table = new Table("Preview of Trigger's Next FireTimes");
        content.addComponent(table);
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Trigger Next FireTime", String.class, defaultValue);
        table.addContainerProperty("Calendar exclusion", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading nextFireTimes preview from triggerKey={} scheduler={}", triggerKey, schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        Trigger trigger = scheduler.getTrigger(triggerKey);
        int maxCount = mySchedule.getMyScheduleSettings().getNumOfFiretimesPreview();
        List<Date> nextFireTimes = scheduler.getNextFireTimes(trigger, new Date(), maxCount);

        int index = 1;
        SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
        for (Date nextFireTime : nextFireTimes) {
            String exclusion = "";
            String calName = trigger.getCalendarName();
            if (calName != null) {
                Calendar calendar = scheduler.getCalendar(calName);
                if (!calendar.isTimeIncluded(nextFireTime.getTime()))
                    exclusion = "EXCLUDED BY CALENDAR: " + calName;
            }
            addTableItem(table, index++, toDateStr(nextFireTime, df), exclusion);
        }
    }

    private String toMapStr(Map map) {
        if (map.size() == 0)
            return "";
        else
            return "" + new TreeMap(map);
    }

    private String toStr(Object item) {
        if (item == null)
            return "";
        else
            return "" + item;
    }

    private String toDateStr(Date date, SimpleDateFormat df) {
        if (date == null)
            return "";
        else
            return df.format(date);
    }

    private void addTableItem(Table table, int itemId, String name, String value) {
        Object[] row = new Object[]{name, value};
        table.addItem(row, itemId);
    }

    private String getMisfireInstructionName(Trigger trigger) {
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
}
