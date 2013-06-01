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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**s
 * JobsWithTriggersContent provide a table view for all JobDetails that have triggers associated.
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class JobsWithTriggersContent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsWithTriggersContent.class);
    MySchedule mySchedule = MySchedule.getInstance();
    MyScheduleUi myScheduleUi;
    String schedulerSettingsName;
    HorizontalLayout toolbar;
    Table table;
    String selectedTriggerKeyName;

    public JobsWithTriggersContent(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
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

        disableToolbarIfNeeded();
    }

    private void disableToolbarIfNeeded() {
        if (selectedTriggerKeyName == null) {
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
                showJobsWithTriggersWindow();
            }
        });
        return button;
    }

    private Button createDeleteButton() {
        Button button = new Button("Delete");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmDialog.show(myScheduleUi, "Are you sure to delete trigger?",
                        new ConfirmDialog.Listener() {
                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    TriggerKey triggerKey = getSelectedTriggerKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    scheduler.unscheduleJob(triggerKey);
                                    myScheduleUi.loadSchedulerScreen(schedulerSettingsName);
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
        table.addContainerProperty("Trigger", String.class, defaultValue);
        table.addContainerProperty("JobDetail", String.class, defaultValue);
        table.addContainerProperty("Type", String.class, defaultValue);
        table.addContainerProperty("Next Run", String.class, defaultValue);
        table.addContainerProperty("Last Run", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading triggers from scheduler {}", schedulerSettingsName);
        MySchedule mySchedule = MySchedule.getInstance();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        List<Trigger> triggers = scheduler.getAllTriggers();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Trigger trigger : triggers) {
            TriggerKey triggerKey = trigger.getKey();
            JobKey jobKey = trigger.getJobKey();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            Date previousFireTime = trigger.getPreviousFireTime();
            String triggerKeyName = triggerKey.getName() + "/" + triggerKey.getGroup();
            Object[] row = new Object[]{
                    triggerKeyName,
                    jobKey.getName() + "/" + jobKey.getGroup(),
                    trigger.getClass().getSimpleName() + "/" + jobDetail.getJobClass().getSimpleName(),
                    df.format(trigger.getNextFireTime()),
                    (previousFireTime == null) ? "" : df.format(previousFireTime)
            };
            table.addItem(row, triggerKeyName);
        }

        // Selectable handler
        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedTriggerKeyName = (String) event.getProperty().getValue();
                disableToolbarIfNeeded();
            }
        });

        // Double click handler - drill down to trigger/job details
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    selectedTriggerKeyName = (String) event.getItemId();
                    showJobsWithTriggersWindow();
                }
            }
        });
    }

    private void showJobsWithTriggersWindow() {
        TriggerKey triggerKey = getSelectedTriggerKey();
        JobsWithTriggersWindow window = new JobsWithTriggersWindow(myScheduleUi, schedulerSettingsName, triggerKey);
        myScheduleUi.addWindow(window);
    }

    private TriggerKey getSelectedTriggerKey() {
        String[] names = StringUtils.split(selectedTriggerKeyName, "/");
        if (names.length != 2)
            throw new RuntimeException("Unable to retrieve trigger: invalid trigger name/group format used.");

        TriggerKey triggerKey = new TriggerKey(names[0], names[1]);
        return triggerKey;
    }
}