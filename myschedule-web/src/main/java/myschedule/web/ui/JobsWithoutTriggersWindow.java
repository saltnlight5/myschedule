package myschedule.web.ui;

import com.vaadin.ui.Table;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * A UI popup window to display JobDetails information (ones without any triggers associated with).
 * This window is opened from the SchedulerScreen.
 */
public class JobsWithoutTriggersWindow extends AbstractWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsWithoutTriggersWindow.class);
    private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private MySchedule mySchedule = MySchedule.getInstance();
    private JobKey jobDetailKey;

    public JobsWithoutTriggersWindow(MyScheduleUi myScheduleUi, String schedulerSettingsName, JobKey jobDetailKey) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        this.jobDetailKey = jobDetailKey;
        initJobDetailTable();
        setCaption("JobDetail Properties");
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

        int index = 1;
        addTableItem(table, index++, "Job Key", "" + jobDetailKey);
        addTableItem(table, index++, "Description", "" + toStr(job.getDescription()));
        addTableItem(table, index++, "Class", "" + job.getJobClass());
        addTableItem(table, index++, "JobDataMap", "" + toMapStr(job.getJobDataMap()));

        // Shrink the table height to fit data rows size.
        table.setPageLength(table.size());
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

    private void addTableItem(Table table, int itemId, String name, String value) {
        Object[] row = new Object[]{name, value};
        table.addItem(row, itemId);
    }
}
