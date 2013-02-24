package myschedule.web.ui;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        jobsTable.addContainerProperty("Job", String.class, defaultValue);
        jobsTable.addContainerProperty("Type", Integer.class, defaultValue);
        jobsTable.addContainerProperty("Next Run", Integer.class, defaultValue);
        jobsTable.addContainerProperty("Last Run", Integer.class, defaultValue);

//        // Fill table data
//        MySchedule mySchedule = MySchedule.getInstance();
//        List<String> names = mySchedule.getSchedulerSettingsNames();
//        LOGGER.debug("Loading {} scheduler config settings.", names.size());
//        for (String settingsName : names) {
//            SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
//            LOGGER.debug("Populating dashboard with: {}", settings);
//            SchedulerTemplate scheduler = mySchedule.getScheduler(settingsName);
//            SchedulerStatus status = MySchedule.getSchedulerStatus(scheduler);
//            Integer jobCount = (status == SchedulerStatus.STANDBY || status == SchedulerStatus.RUNNING) ?
//                scheduler.getAllTriggers().size() : 0;
//            Object[] row = new Object[] {
//                "", // Action
//                settings.getSchedulerFullName(),
//                status.toString(),
//                jobCount
//            };
//            jobsTable.addItem(row, settingsName);
//        }

        // Add table to this UI screen
        addComponent(jobsTable);
    }
}
