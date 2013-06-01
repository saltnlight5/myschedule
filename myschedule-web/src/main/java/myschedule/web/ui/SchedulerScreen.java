package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.*;
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

/**
 * UI screen that display a table of jobs found in a scheduler. This list of jobs (a Trigger and a JobDetail)
 * found in Quartz Scheduler. Note that each Trigger will have its JobDetail associated. The combination of a trigger
 * and a jobDetail means a "job" in Quartz term.
 * <p>User may also use this screen and its tool bar to display further detail information about this particular
 * scheduler.</p>
 * <p>This screen should also provide a link to open a ScriptConsole for this particular scheduler.</p>
 */
public class SchedulerScreen extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerScreen.class);
    private static final long serialVersionUID = 1L;
    private MySchedule mySchedule = MySchedule.getInstance();
    private MyScheduleUi myScheduleUi;
    private String schedulerSettingsName;
    private SchedulerContent content;

    public SchedulerScreen(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initContent();
    }

    private void initContent() {
        content = new SchedulerContent();
        addComponent(content);
    }

    private void switchContentToJobsWithTriggers() {
        content.removeAllComponents();
        content.addComponent(new JobsWithTriggersContent());
    }

    private void switchContentToJobsWithoutTriggers() {
        content.removeAllComponents();
        content.addComponent(new JobsWithoutTriggersContent());
    }

    private Button createJobsWithoutTriggersButton() {
        Button button = new Button("Jobs Without Triggers");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                switchContentToJobsWithoutTriggers();
            }
        });
        return button;
    }

    class JobsWithTriggersContent extends VerticalLayout {
        HorizontalLayout toolbar;
        Table table;
        String selectedTriggerKeyName;

        public JobsWithTriggersContent() {
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
                    showTriggerAndJobDetailsWindow();
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
                        showTriggerAndJobDetailsWindow();
                    }
                }
            });
        }

        private void showTriggerAndJobDetailsWindow() {
            TriggerKey triggerKey = getSelectedTriggerKey();
            TriggerAndJobDetailWindow window = new TriggerAndJobDetailWindow(myScheduleUi, schedulerSettingsName, triggerKey);
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

    class JobsWithoutTriggersContent extends VerticalLayout {

    }

    class SchedulerContent extends VerticalLayout {
        TabSheet tabSheet;
        VerticalLayout jobsWithTriggersContent = new VerticalLayout();
        VerticalLayout jobsWithoutTriggersContent = new VerticalLayout();
        VerticalLayout scriptConsoleContent = new VerticalLayout();
        public SchedulerContent() {
            tabSheet = new TabSheet();
            addComponent(tabSheet);

            tabSheet.addTab(jobsWithTriggersContent, "Jobs with Triggers");
            tabSheet.addTab(jobsWithoutTriggersContent, "Jobs without Triggers");
            tabSheet.addTab(scriptConsoleContent, "Script Console");

            tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
                @Override
                public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                    TabSheet tabSheet = selectedTabChangeEvent.getTabSheet();
                    VerticalLayout selectedContent = (VerticalLayout)tabSheet.getSelectedTab();
                    if (selectedContent == jobsWithTriggersContent) {
                        switchJobsWithTriggersContent();
                    } else if (selectedContent == jobsWithoutTriggersContent) {
                        switchJobsWithoutTriggersContent();
                    } else if (selectedContent == scriptConsoleContent) {
                        ScriptConsoleWindow console = new ScriptConsoleWindow(myScheduleUi, schedulerSettingsName);
                        myScheduleUi.addWindow(console);

                        // script console is a popup window, and we will switch the content to jobsWithTriggersContent view.
                        tabSheet.setSelectedTab(jobsWithTriggersContent);
                    }
                }
            });

            // default trigger first tab selection.
            tabSheet.setSelectedTab(jobsWithTriggersContent);
            switchJobsWithTriggersContent();
        }

        void switchJobsWithTriggersContent() {
            jobsWithTriggersContent.removeAllComponents();
            jobsWithTriggersContent.addComponent(new JobsWithTriggersContent());

            // Clean up other tab resources
            jobsWithoutTriggersContent.removeAllComponents();
        }

        void switchJobsWithoutTriggersContent() {
            jobsWithoutTriggersContent.removeAllComponents();
            jobsWithoutTriggersContent.addComponent(new JobsWithoutTriggersContent());

            // Clean up other tab resources
            jobsWithTriggersContent.removeAllComponents();
        }
    }
}
