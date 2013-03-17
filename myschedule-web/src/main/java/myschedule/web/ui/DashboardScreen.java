package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import myschedule.web.SchedulerStatus;
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
    private SchedulerButtonGroup schedulerButtonGroup;
    private Table table;
    private MySchedule mySchedule = MySchedule.getInstance();

    public DashboardScreen(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;
        initToolbar();
        initSchedulersTable();
	}

    private void initToolbar() {
        toolbar = new HorizontalLayout();
        addComponent(toolbar);

        schedulerButtonGroup = new SchedulerButtonGroup();
        toolbar.addComponent(schedulerButtonGroup);
    }

    private void initSchedulersTable() {
        table = new Table();
        addComponent(table);

        table.setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Scheduler", Button.class, defaultValue);
        table.addContainerProperty("Config ID", String.class, defaultValue);
        table.addContainerProperty("Status", String.class, defaultValue);
        table.addContainerProperty("Job Counts", Integer.class, defaultValue);

        // Fill table data
        List<String> names = mySchedule.getSchedulerSettingsNames();
        for (String settingsName : names) {
            LOGGER.debug("Adding scheduler settings {} to dashboard.", settingsName);
            SchedulerSettings settings = mySchedule.getSchedulerSettings(settingsName);
            SchedulerTemplate scheduler = mySchedule.getScheduler(settingsName);
            SchedulerStatus status = MySchedule.getSchedulerStatus(scheduler);
            Integer jobCount = 0;
            Button schedulerNameComponent = createSchedulerNameComponent(settings.getSchedulerFullName(), settingsName);

            if (status == SchedulerStatus.RUNNING || status == SchedulerStatus.STANDBY) {
                jobCount = scheduler.getAllTriggers().size();
            } else {
                schedulerNameComponent.setEnabled(false);
            }

            Object[] row = new Object[] {
                schedulerNameComponent,
                settingsName,
                status.toString(),
                jobCount
            };
            table.addItem(row, settingsName);
        }

        // Selectable action
        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String settingsName = (String)event.getProperty().getValue();
                schedulerButtonGroup.updateSelectedSettingsName(settingsName);
            }
        });
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

    class SchedulerButtonGroup extends HorizontalLayout {
        Button edit = createEditButton();
        Button delete = createDeleteButton();
        Button init = createInitButton();
        Button start = createStartButton();
        Button standby = createStandbyButton();
        Button shutdown = createShutdownButton();
        String selectedSettingsName;

        public SchedulerButtonGroup() {
            addComponent(createNewButton());
            addComponent(edit);
            addComponent(delete);
            addComponent(init);
            addComponent(start);
            addComponent(standby);
            addComponent(shutdown);

            updateSelectedSettingsName(null);
        }

        void updateSelectedSettingsName(String settingsName) {
            this.selectedSettingsName = settingsName;

            if (selectedSettingsName == null) {
                disableAll(edit, delete, init, start, standby, shutdown);
            } else {
                enableAll(edit, delete);

                SchedulerTemplate scheduler = mySchedule.getScheduler(selectedSettingsName);
                SchedulerStatus status = MySchedule.getSchedulerStatus(scheduler);

                if (status == SchedulerStatus.RUNNING) {
                    disableAll(init, start);
                    enableAll(standby, shutdown);
                } else if (status == SchedulerStatus.SHUTDOWN) {
                    enableAll(init, start);
                    disableAll(standby, shutdown);
                } else if (status == SchedulerStatus.STANDBY) {
                    disableAll(init, standby);
                    enableAll(standby, shutdown);
                }
            }
        }

        private void enableAll(Button ... buttons) {
            for (Button b : buttons)
                b.setEnabled(true);
        }

        private void disableAll(Button ... buttons) {
            for (Button b : buttons)
                b.setEnabled(false);
        }

        private Button createNewButton() {
            Button button = new Button("New");
            button.addClickListener(new Button.ClickListener()
            {
                @Override
                public void buttonClick(Button.ClickEvent event)
                {
                    myScheduleUi.addWindow(new NewSchedulerWindow(myScheduleUi));
                }
            });
            return button;
        }

        private Button createEditButton() {
            Button button = new Button("Edit");
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    // The editor will refresh the dashboard upon close.
                    myScheduleUi.addWindow(new EditSchedulerWindow(myScheduleUi, selectedSettingsName));
                }
            });
            return button;
        }

        private Button createDeleteButton() {
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
                                    mySchedule.deleteSchedulerSettings(selectedSettingsName);
                                    table.removeItem(selectedSettingsName);
                                    myScheduleUi.loadDashboardScreen();
                                }
                            }
                        }
                    );
                }
            });
            return button;
        }

        private Button createInitButton() {
            Button button = new Button("Init");
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    SchedulerSettings settings = mySchedule.getSchedulerSettings(selectedSettingsName);
                    mySchedule.createScheduler(settings);
                    myScheduleUi.loadDashboardScreen();
                }
            });
            return button;
        }

        private Button createStartButton() {
            Button button = new Button("Start");
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    mySchedule.getScheduler(selectedSettingsName).start();
                    myScheduleUi.loadDashboardScreen();
                }
            });
            return button;
        }

        private Button createStandbyButton() {
            Button button = new Button("Standby");
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ConfirmDialog.show(myScheduleUi, "Are you sure to standby?",
                        new ConfirmDialog.Listener()
                        {
                            public void onClose(ConfirmDialog dialog)
                            {
                                if (dialog.isConfirmed()) {
                                    mySchedule.getScheduler(selectedSettingsName).standby();
                                    myScheduleUi.loadDashboardScreen();
                                }
                            }
                        }
                    );
                }
            });
            return button;
        }

        private Button createShutdownButton() {
            Button button = new Button("Shutdown");
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ConfirmDialog.show(myScheduleUi, "Are you sure to shutdown?",
                        new ConfirmDialog.Listener()
                        {
                            public void onClose(ConfirmDialog dialog)
                            {
                                if (dialog.isConfirmed()) {
                                    mySchedule.shutdownScheduler(selectedSettingsName);
                                    myScheduleUi.loadDashboardScreen();
                                }
                            }
                        }
                    );
                }
            });
            return button;
        }
    }
}
