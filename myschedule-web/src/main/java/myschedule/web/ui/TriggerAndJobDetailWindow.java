package myschedule.web.ui;

import com.vaadin.ui.Table;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * A UI popup window to display Trigger and JobDetails information.
 */
public class TriggerAndJobDetailWindow extends AbstractWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerAndJobDetailWindow.class);
    private static final long serialVersionUID = 1L;
    //private MyScheduleUi myScheduleUi;
    private String schedulerSettingsName;
    private MySchedule mySchedule = MySchedule.getInstance();
    private Table triggerDetailsTable;
    private Table jobDetailTable;
    private TriggerKey triggerKey;
    private JobKey jobDetailKey;

    public TriggerAndJobDetailWindow(MyScheduleUi myScheduleUi, String schedulerSettingsName, TriggerKey triggerKey) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        this.triggerKey = triggerKey;
        initTriggerDetailsTable();
        initJobDetailTable();
        setCaption("Trigger and JobDetail Properties");
    }

    protected void initTriggerDetailsTable() {
        triggerDetailsTable = new Table("Trigger Information");
        content.addComponent(triggerDetailsTable);

        Table table = triggerDetailsTable; // Use short and local var.
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Property Name", String.class, defaultValue);
        table.addContainerProperty("Property Value", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading triggerKey={} from scheduler {}", triggerKey, schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        Trigger trigger = scheduler.getTrigger(triggerKey);
        jobDetailKey = trigger.getJobKey();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 1;
        addTableItem(table, index++, "Trigger Key", "" + triggerKey);
        addTableItem(table, index++, "Description", "" + toStr(trigger.getDescription()));
        addTableItem(table, index++, "Class", "" + trigger.getClass());
        addTableItem(table, index++, "Misfire Instruction", "" + trigger.getMisfireInstruction());
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
        jobDetailTable = new Table("JobDetail Information");
        jobDetailTable.setSizeFull();
        content.addComponent(jobDetailTable);

        Table table = jobDetailTable; // Use short and local var.
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Property Name", String.class, defaultValue);
        table.addContainerProperty("Property Value", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading jobDetail={} from scheduler {}", jobDetailKey, schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        JobDetail job = scheduler.getJobDetail(jobDetailKey);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 1;
        addTableItem(table, index++, "Job Key", "" + jobDetailKey);
        addTableItem(table, index++, "Description", "" + toStr(job.getDescription()));
        addTableItem(table, index++, "Class", "" + job.getJobClass());
        addTableItem(table, index++, "JobDataMap", "" + toMapStr(job.getJobDataMap()));
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
}
