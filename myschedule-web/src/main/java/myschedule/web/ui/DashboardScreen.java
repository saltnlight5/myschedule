package myschedule.web.ui;

import com.vaadin.ui.*;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import myschedule.web.SchedulerStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

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
    private HorizontalLayout toolbar;
    private Table table;
    private MySchedule mySchedule = MySchedule.getInstance();

    public DashboardScreen(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;
        initToolbar();
        initSchedulersTable();
	}

    private void initToolbar() {
        toolbar = new HorizontalLayout();
        toolbar.addComponent(createNewSchedulerButton());
        addComponent(toolbar);
    }

    private Button createNewSchedulerButton() {
        Button button = new Button("Create New Scheduler");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                myScheduleUi.addWindow(new NewSchedulerWindow(myScheduleUi));
            }
        });
        return button;
    }

    private void initSchedulersTable() {
        table = new Table();
        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Scheduler", Button.class, defaultValue);
        table.addContainerProperty("Config ID", String.class, defaultValue);
        table.addContainerProperty("Status", String.class, defaultValue);
        table.addContainerProperty("Job Counts", Integer.class, defaultValue);
        table.addContainerProperty("Actions", HorizontalLayout.class, defaultValue);

        // Fill table data
        List<String> names = mySchedule.getSchedulerSettingsNames();
        for (String settingsName : names) {
            LOGGER.debug("Adding scheduler settings {} to dashboard.", settingsName);
            SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
            SchedulerTemplate scheduler = mySchedule.getScheduler(settingsName);
            SchedulerStatus status = MySchedule.getSchedulerStatus(scheduler);
            Integer jobCount = 0;
            Button schedulerNameComponent = createSchedulerNameComponent(settings.getSchedulerFullName(), settingsName);

            HorizontalLayout actions = new HorizontalLayout();
            actions.addComponent(createEditButton(settingsName));
            actions.addComponent(createDeleteButton(settingsName));

            if (status == SchedulerStatus.STANDBY || status == SchedulerStatus.RUNNING) {
                jobCount = scheduler.getAllTriggers().size();
                actions.addComponent(createShutdownButton(settingsName));
            } else {
                schedulerNameComponent.setEnabled(false);
                actions.addComponent(createInitButton(settingsName));
            }

            Object[] row = new Object[] {
                schedulerNameComponent,
                settingsName,
                status.toString(),
                jobCount,
                actions
            };
            table.addItem(row, settingsName);
        }

        // Add table to this UI screen
        addComponent(table);
    }

    private Button createEditButton(final String settingsName) {
        Button button = new Button("Edit");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                myScheduleUi.addWindow(new EditSchedulerWindow(myScheduleUi, settingsName));
            }
        });
        return button;
    }

    private Button createDeleteButton(final String settingsName) {
        Button button = new Button("Delete");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmDialog.show(myScheduleUi, "Are you sure to delete?",
                    new ConfirmDialog.Listener()
                    {
                        public void onClose(ConfirmDialog dialog)
                        {
                            if (dialog.isConfirmed()) {
                                mySchedule.deleteSchedulerSettings(settingsName);
                                table.removeItem(settingsName);
                            }
                        }
                    }
                );
            }
        });
        return button;
    }

    private Button createInitButton(final String settingsName) {
        Button button = new Button("Init");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
                mySchedule.createScheduler(settingsName, settings);
                myScheduleUi.loadDashboardScreen();
            }
        });
        return button;
    }

    private Button createShutdownButton(final String settingsName) {
        Button button = new Button("Shutdown");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmDialog.show(myScheduleUi, "Are you sure to shutdown?",
                    new ConfirmDialog.Listener() {
                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                mySchedule.shutdownScheduler(settingsName);
                                myScheduleUi.loadDashboardScreen();
                            }
                        }
                    }
                );
            }
        });
        return button;
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
