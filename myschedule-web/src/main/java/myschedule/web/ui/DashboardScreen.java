package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import myschedule.web.SchedulerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UI screen for displaying a dashboard of all schedulers in a table view. This is the default main screen for the
 * MySchedule application. User should see all the configured scheduler in this screen with their status and actions.
 * This screen should also have a toolbar to allow user to add new scheduler config entry into this scheduler table.
 */
public class DashboardScreen extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardScreen.class);
	private static final long serialVersionUID = 1L;
    private MyScheduleUi myScheduleUi;
    private Table table;

    public DashboardScreen(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;

        initSchedulersTable();
	}

    private void initSchedulersTable() {
        table = new Table();
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Actions", String.class, defaultValue);
        table.addContainerProperty("Scheduler", Button.class, defaultValue);
        table.addContainerProperty("Status", String.class, defaultValue);
        table.addContainerProperty("Job Counts", Integer.class, defaultValue);

        // Fill table data
        MySchedule mySchedule = MySchedule.getInstance();
        List<String> names = mySchedule.getSchedulerSettingsNames();
        LOGGER.debug("Loading {} scheduler config settings.", names.size());
        for (String settingsName : names) {
            SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
            LOGGER.debug("Populating dashboard with: {}", settings);
            SchedulerTemplate scheduler = mySchedule.getScheduler(settingsName);
            SchedulerStatus status = MySchedule.getSchedulerStatus(scheduler);
            Integer jobCount = 0;
            Button schedulerNameComponent = createSchedulerNameComponent(settings.getSchedulerFullName(), settingsName);

            if (status == SchedulerStatus.STANDBY || status == SchedulerStatus.RUNNING) {
                jobCount = scheduler.getAllTriggers().size();
            } else {
                schedulerNameComponent.setEnabled(false);
            }

            Object[] row = new Object[] {
                "", // Action
                schedulerNameComponent,
                status.toString(),
                jobCount
            };
            table.addItem(row, settingsName);
        }

        // Add table to this UI screen
        addComponent(table);
    }

    private Button createSchedulerNameComponent(String schedulerFullName, final String settingsName) {
        Button label = new Button(schedulerFullName);
        label.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                DashboardScreen.this.myScheduleUi.loadSchedulerScreen(settingsName);
            }
        });
        return label;
    }
}
