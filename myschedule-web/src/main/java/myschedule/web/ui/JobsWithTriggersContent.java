package myschedule.web.ui;

import com.vaadin.data.Item;
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
import org.quartz.Trigger;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**s
 * JobsWithTriggersContents provide a table view for all JobDetails that have triggers associated.
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class JobsWithTriggersContent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsWithTriggersContent.class);
    MySchedule mySchedule = MySchedule.getInstance();
    MyScheduleUi myScheduleUi;
    String schedulerSettingsName;
    HorizontalLayout toolbar;
    HorizontalLayout tableRowActionButtonsGroup;
    Table table;
    String selectedTriggerKeyName;
    Button pauseOrResumeButton;

    public JobsWithTriggersContent(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
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

        pauseOrResumeButton = createPauseOrResumeButton();
        tableRowActionButtonsGroup.addComponent(pauseOrResumeButton);

        disableToolbarIfNeeded("STATE_NORMAL");
    }

    private void disableToolbarIfNeeded(String triggerStateName) {
        if (selectedTriggerKeyName == null) {
            tableRowActionButtonsGroup.setEnabled(false);
        } else {
            // Check and ensure Pause/Resume button has the right label.
            int triggerState = getTriggerStateFromName(triggerStateName);
            if (triggerState == Trigger.STATE_PAUSED)
                pauseOrResumeButton.setCaption("Resume");
            else
                pauseOrResumeButton.setCaption("Pause");
            tableRowActionButtonsGroup.setEnabled(true);
        }
    }

    private int getTriggerStateFromName(String triggerStateName) {
        if("NORMAL".equals(triggerStateName) || "NONE".equals(triggerStateName))
            return Trigger.STATE_NORMAL;
        else if("BLOCKED".equals(triggerStateName))
            return Trigger.STATE_BLOCKED;
        else if("COMPLETE".equals(triggerStateName))
            return Trigger.STATE_COMPLETE;
        else if("ERROR".equals(triggerStateName))
            return Trigger.STATE_ERROR;
        else if("PAUSED".equals(triggerStateName))
            return Trigger.STATE_PAUSED;
        return -1;
    }
    private String getTriggerStateName(int triggerState) {
        if(triggerState == Trigger.STATE_NORMAL || triggerState == Trigger.STATE_NONE)
            return "NORMAL";
        else if(triggerState == Trigger.STATE_BLOCKED)
            return "BLOCKED";
        else if(triggerState == Trigger.STATE_COMPLETE)
            return "COMPLETE";
        else if(triggerState == Trigger.STATE_ERROR)
            return "ERROR";
        else if(triggerState == Trigger.STATE_PAUSED)
            return "PAUSED";
        return "UNKONWN";
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
                                    Key triggerKey = getSelectedTriggerKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    scheduler.unscheduleJob(triggerKey.getName(), triggerKey.getGroup());

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
                                    Key triggerKey = getSelectedTriggerKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    Trigger trigger = scheduler.getTrigger(triggerKey.getName(), triggerKey.getGroup());
                                    scheduler.triggerJob(trigger.getJobName(), trigger.getJobGroup());

                                    reloadTableContent();
                                }
                            }
                        }
                );
            }
        });
        return button;
    }

    private Button createPauseOrResumeButton() {
        Button button = new Button("Pause");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final String caption = event.getButton().getCaption();
                String msg = caption.equals("Pause") ?
                        "Are you sure to pause it now?" :
                        "Are you sure to resume it now?";
                ConfirmDialog.show(myScheduleUi, msg,
                        new ConfirmDialog.Listener() {
                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    Key triggerKey = getSelectedTriggerKey();
                                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                                    if (caption.equals("Pause")) {
                                        scheduler.pauseTrigger(triggerKey.getName(), triggerKey.getGroup());
                                    } else {
                                        scheduler.resumeTrigger(triggerKey.getName(), triggerKey.getGroup());
                                    }
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
        table.addContainerProperty("Trigger", String.class, defaultValue);
        table.addContainerProperty("JobDetail", String.class, defaultValue);
        table.addContainerProperty("Type", String.class, defaultValue);
        table.addContainerProperty("Next Run", String.class, defaultValue);
        table.addContainerProperty("Last Run", String.class, defaultValue);
        table.addContainerProperty("Status", String.class, defaultValue);

        // Selectable handler
        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedTriggerKeyName = (String) event.getProperty().getValue();
                Item item = table.getItem(selectedTriggerKeyName);
                if (item != null) {
                    String triggerStateName = (String)item.getItemProperty("Status").getValue();
                    disableToolbarIfNeeded(triggerStateName);
                }
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

        // Fill table data
        reloadTableContent();
    }

    private void reloadTableContent() {
        table.removeAllItems();
        LOGGER.debug("Loading triggers from scheduler {}", schedulerSettingsName);
        MySchedule mySchedule = MySchedule.getInstance();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        List<Trigger> triggers = scheduler.getAllTriggers();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Trigger trigger : triggers) {
            Key triggerKey = trigger.getKey();
            Key jobKey = new Key(trigger.getJobName(), trigger.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey.getName(), jobKey.getGroup());
            Date nextFireTime = trigger.getNextFireTime();
            Date previousFireTime = trigger.getPreviousFireTime();
            String triggerKeyName = triggerKey.getName() + "/" + triggerKey.getGroup();
            String triggerStateName = getTriggerStateName(scheduler.getTriggerState(trigger.getJobName(), trigger.getJobGroup()));
            Object[] row = new Object[]{
                    triggerKeyName,
                    jobKey.getName() + "/" + jobKey.getGroup(),
                    trigger.getClass().getSimpleName() + "/" + jobDetail.getJobClass().getSimpleName(),
                    (nextFireTime == null) ? "" : df.format(nextFireTime),
                    (previousFireTime == null) ? "" : df.format(previousFireTime),
                    triggerStateName
            };
            table.addItem(row, triggerKeyName);
        }
    }

    private void showJobsWithTriggersWindow() {
        Key triggerKey = getSelectedTriggerKey();
        JobsWithTriggersWindow window = new JobsWithTriggersWindow(myScheduleUi, schedulerSettingsName, triggerKey);
        myScheduleUi.addWindow(window);
    }

    private Key getSelectedTriggerKey() {
        String[] names = StringUtils.split(selectedTriggerKeyName, "/");
        if (names.length != 2)
            throw new RuntimeException("Unable to retrieve trigger: invalid trigger name/group format used.");

        Key triggerKey = new Key(names[0], names[1]);
        return triggerKey;
    }
}