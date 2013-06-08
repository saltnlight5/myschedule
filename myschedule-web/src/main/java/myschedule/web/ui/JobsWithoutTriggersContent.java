package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDetail;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.List;

/**
 * JobsWithoutTriggersContent provides a table view for all JobDetails that do not have triggers associated.
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class JobsWithoutTriggersContent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsWithoutTriggersContent.class);
    MySchedule mySchedule = MySchedule.getInstance();
    MyScheduleUi myScheduleUi;
    String schedulerSettingsName;
    HorizontalLayout toolbar;
    HorizontalLayout tableRowActionButtonsGroup;
    Table table;
    String selectedKeyName;

    public JobsWithoutTriggersContent(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initToolbar();
        initJobsTable();
    }

    private void initToolbar() {
        toolbar = new HorizontalLayout();
        addComponent(toolbar);

        toolbar.addComponent(createRefreshButton());

        tableRowActionButtonsGroup = new HorizontalLayout();
        toolbar.addComponent(tableRowActionButtonsGroup);

        tableRowActionButtonsGroup.addComponent(createViewDetailsButton());
        tableRowActionButtonsGroup.addComponent(createDeleteButton());
        tableRowActionButtonsGroup.addComponent(createRunItNowButton());

        disableToolbarIfNeeded();
    }

    private void disableToolbarIfNeeded() {
        if (selectedKeyName == null) {
            tableRowActionButtonsGroup.setEnabled(false);
        } else {
            tableRowActionButtonsGroup.setEnabled(true);
        }
    }

    private Button createRefreshButton() {
        Button button = new Button("Refresh");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                reloadTableContent();
            }
        });
        return button;
    }

    private Button createViewDetailsButton() {
        Button button = new Button("View Details");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showJobsWithoutTriggersWindow();
            }
        });
        return button;
    }

    private Button createDeleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmDialog.show(myScheduleUi, "Are you sure to delete job detail?",
                        new ConfirmDialog.Listener() {
                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    Key key = getSelectedKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    scheduler.deleteJob(key.getName(), key.getGroup());

                                    reloadTableContent();
                                }
                            }
                        }
                );
            }
        });
        return button;
    }

    private Button createRunItNowButton() {
        Button button = new Button("Run It Now");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmDialog.show(myScheduleUi, "Are you sure to run it now?",
                        new ConfirmDialog.Listener() {
                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    Key key = getSelectedKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    scheduler.triggerJob(key.getName(), key.getGroup());

                                    reloadTableContent();
                                }
                            }
                        }
                );
            }
        });
        return button;
    }

    private void initJobsTable() {
        table = new Table();
        addComponent(table);

        table.setSizeFull();
        table.setImmediate(true);
        table.setSelectable(true);

        Object defaultValue = null; // Not used.
        table.addContainerProperty("JobDetail", String.class, defaultValue);
        table.addContainerProperty("Type", String.class, defaultValue);

        // Selectable handler
        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedKeyName = (String) event.getProperty().getValue();
                disableToolbarIfNeeded();
            }
        });

        // Double click handler - drill down to trigger/job details
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    selectedKeyName = (String) event.getItemId();
                    showJobsWithoutTriggersWindow();
                }
            }
        });

        reloadTableContent();
    }

    private void reloadTableContent() {
        table.removeAllItems();
        // Fill table data
        LOGGER.debug("Loading jobDetails from scheduler {}", schedulerSettingsName);
        MySchedule mySchedule = MySchedule.getInstance();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        List<JobDetail> jobDetails = scheduler.getAllJobDetails();
        for (JobDetail jobDetail : jobDetails) {
            if (scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup()).length > 0) {
                continue;
            }
            Key Key = jobDetail.getKey();
            String KeyName = Key.getName() + "/" + Key.getGroup();
            Object[] row = new Object[]{
                    KeyName,
                    jobDetail.getClass().getSimpleName() + "/" + jobDetail.getJobClass().getSimpleName()
            };
            table.addItem(row, KeyName);
        }

    }

    private void showJobsWithoutTriggersWindow() {
        Key Key = getSelectedKey();
        JobsWithoutTriggersWindow window = new JobsWithoutTriggersWindow(myScheduleUi, schedulerSettingsName, Key);
        myScheduleUi.addWindow(window);
    }

    private Key getSelectedKey() {
        String[] names = StringUtils.split(selectedKeyName, "/");
        if (names.length != 2)
            throw new RuntimeException("Unable to retrieve trigger: invalid trigger name/group format used.");

        Key Key = new Key(names[0], names[1]);
        return Key;
    }
}