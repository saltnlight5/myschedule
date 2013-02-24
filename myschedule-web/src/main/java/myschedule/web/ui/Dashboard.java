package myschedule.web.ui;

import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UI screen for the schedulers dashboard. This is the default main screen for the MySchedule application.
 *
 * User should see all the configured schedulers in this screen with their status and actions. The screen
 * should also have a toolbar to allow add new scheduler config entry.
 */
public class Dashboard extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dashboard.class);
	private static final long serialVersionUID = 1L;
    private Table schedulers;


    public Dashboard() {
        initSchedulers();
	}

    private void initSchedulers() {
        schedulers = new Table();
        schedulers.setSizeFull();

        Object defaultValue = null; // Not used.
        schedulers.addContainerProperty("Actions", String.class, defaultValue);
        schedulers.addContainerProperty("Scheduler", String.class, defaultValue);
        schedulers.addContainerProperty("Status", String.class, defaultValue);
        schedulers.addContainerProperty("Job Counts", Integer.class, defaultValue);

        // Fill table data
        MySchedule mySchedule = MySchedule.getInstance();
        List<String> names = mySchedule.getSchedulerSettingsNames();
        LOGGER.info("Loading {} scheduler config settings.", names.size());
        for (String settingsName : names) {
            SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
            LOGGER.info("Populating dashboard with: {}", settings);
            String status = "?"; // TODO: get status
            Integer jobCount = new Integer(0); // TODO we need to calculate this value.
            Object[] row = new Object[] {
                "", // Action
                settings.getSchedulerFullName(),
                status,
                jobCount
            };
            schedulers.addItem(row, settingsName);
        }

        // Add to this panel
        addComponent(schedulers);
    }
}
