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
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
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
    Table table;
    String selectedJobKeyName;

    public JobsWithoutTriggersContent(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initToolbar();
        initJobsTable();
    }

    private void initToolbar() {
        toolbar = new HorizontalLayout();
        addComponent(toolbar);

        toolbar.addComponent(createViewDetailsButton());
        toolbar.addComponent(createDeleteButton());
        toolbar.addComponent(createRunItNowButton());

        disableToolbarIfNeeded();
    }

    private void disableToolbarIfNeeded() {
        if (selectedJobKeyName == null) {
            toolbar.setEnabled(false);
        } else {
            toolbar.setEnabled(true);
        }
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
                                    JobKey jobKey = getSelectedJobKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    scheduler.deleteJob(jobKey);

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
                                    JobKey jobKey = getSelectedJobKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    scheduler.triggerJob(jobKey);

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
                selectedJobKeyName = (String) event.getProperty().getValue();
                disableToolbarIfNeeded();
            }
        });

        // Double click handler - drill down to trigger/job details
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    selectedJobKeyName = (String) event.getItemId();
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
            if (scheduler.getTriggersOfJob(jobDetail.getKey()).size() > 0) {
                continue;
            }
            JobKey jobKey = jobDetail.getKey();
            String jobKeyName = jobKey.getName() + "/" + jobKey.getGroup();
            Object[] row = new Object[]{
                    jobKeyName,
                    jobDetail.getClass().getSimpleName() + "/" + jobDetail.getJobClass().getSimpleName()
            };
            table.addItem(row, jobKeyName);
        }

    }

    private void showJobsWithoutTriggersWindow() {
        JobKey jobKey = getSelectedJobKey();
        JobsWithoutTriggersWindow window = new JobsWithoutTriggersWindow(myScheduleUi, schedulerSettingsName, jobKey);
        myScheduleUi.addWindow(window);
    }

    private JobKey getSelectedJobKey() {
        String[] names = StringUtils.split(selectedJobKeyName, "/");
        if (names.length != 2)
            throw new RuntimeException("Unable to retrieve trigger: invalid trigger name/group format used.");

        JobKey jobKey = new JobKey(names[0], names[1]);
        return jobKey;
    }
}