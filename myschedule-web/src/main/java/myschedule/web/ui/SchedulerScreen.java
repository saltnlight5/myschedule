package myschedule.web.ui;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import myschedule.web.SchedulerStatus;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UI screen that display a table of jobs found in a scheduler. This list of jobs are all Trigger found in Quartz
 * Scheduler. Note that each Trigger will have its JobDetail associated. The combination means a "job" in the scheduler.
 * User may also use this screen and its tool bar to display further detail information about this particular scheduler.
 */
public class SchedulerScreen extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerScreen.class);
	private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private Table jobsTable;

    public SchedulerScreen(String schedulerSettingsName) {
        this.schedulerSettingsName = schedulerSettingsName;

        initJobsTable();
    }

    private void initJobsTable() {
        jobsTable = new Table();
        jobsTable.setSizeFull();

        Object defaultValue = null; // Not used.
        jobsTable.addContainerProperty("Actions", String.class, defaultValue);
        jobsTable.addContainerProperty("Trigger", String.class, defaultValue);
        jobsTable.addContainerProperty("JobDetail", String.class, defaultValue);
        jobsTable.addContainerProperty("Type", Integer.class, defaultValue);
        jobsTable.addContainerProperty("Next Run", Integer.class, defaultValue);
        jobsTable.addContainerProperty("Last Run", Integer.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading triggers from scheduler {}", schedulerSettingsName);
        MySchedule mySchedule = MySchedule.getInstance();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        List<Trigger> triggers = scheduler.getAllTriggers();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        for (Trigger trigger : triggers) {
            TriggerKey triggerKey = trigger.getKey();
            JobKey jobKey = trigger.getJobKey();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            Date previousFireTime = trigger.getPreviousFireTime();
            Object[] row = new Object[] {
                "", // Action
                triggerKey.toString(),
                jobKey.toString(),
                trigger.getClass().getSimpleName() + "/" + jobDetail.getClass().getSimpleName(),
                df.format(trigger.getNextFireTime()),
                (previousFireTime == null) ? "" : df.format(previousFireTime)
            };
            jobsTable.addItem(row, triggerKey.toString());
        }

        // Add table to this UI screen
        addComponent(jobsTable);
    }
}
