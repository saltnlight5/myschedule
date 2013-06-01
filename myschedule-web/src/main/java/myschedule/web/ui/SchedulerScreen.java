package myschedule.web.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * UI screen that display a table of jobs found in a scheduler. This list of jobs (a Trigger and a JobDetail)
 * found in Quartz Scheduler. Note that each Trigger will have its JobDetail associated. The combination of a trigger
 * and a jobDetail means a "job" in Quartz term.
 * <p>User may also use this screen and its tool bar to display further detail information about this particular
 * scheduler.</p>
 * <p>This screen should also provide a link to open a ScriptConsole for this particular scheduler.</p>
 */
public class SchedulerScreen extends VerticalLayout {
    private static final long serialVersionUID = 1L;
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

    class SchedulerContent extends VerticalLayout {
        TabSheet tabSheet;
        VerticalLayout jobsWithTriggersContent = new VerticalLayout();
        VerticalLayout jobsWithoutTriggersContent = new VerticalLayout();
        VerticalLayout jobsRunningContent = new VerticalLayout();
        VerticalLayout scriptConsoleContent = new VerticalLayout();
        public SchedulerContent() {
            tabSheet = new TabSheet();
            addComponent(tabSheet);

            tabSheet.addTab(jobsWithTriggersContent, "Jobs with Triggers");
            tabSheet.addTab(jobsWithoutTriggersContent, "Jobs without Triggers");
            tabSheet.addTab(jobsRunningContent, "Current Executing Jobs");
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
                    } else if (selectedContent == jobsRunningContent) {
                        switchJobsRunningContent();
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
            jobsWithTriggersContent.addComponent(new JobsWithTriggersContent(myScheduleUi, schedulerSettingsName));

            // Clean up other tab resources
            jobsWithoutTriggersContent.removeAllComponents();
            jobsRunningContent.removeAllComponents();
        }

        void switchJobsWithoutTriggersContent() {
            jobsWithoutTriggersContent.removeAllComponents();
            jobsWithoutTriggersContent.addComponent(new JobsWithoutTriggersContent(myScheduleUi, schedulerSettingsName));

            // Clean up other tab resources
            jobsWithTriggersContent.removeAllComponents();
            jobsRunningContent.removeAllComponents();
        }

        void switchJobsRunningContent() {
            jobsRunningContent.removeAllComponents();
            jobsRunningContent.addComponent(new JobsRunningContent(myScheduleUi, schedulerSettingsName));

            // Clean up other tab resources
            jobsWithoutTriggersContent.removeAllComponents();
            jobsWithTriggersContent.removeAllComponents();
        }
    }
}
