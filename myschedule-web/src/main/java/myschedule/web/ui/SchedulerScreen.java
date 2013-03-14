package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UI screen that display a table of jobs found in a scheduler. This list of jobs are all Trigger found in Quartz
 * Scheduler. Note that each Trigger will have its JobDetail associated. The combination means a "job" in the scheduler.
 * User may also use this screen and its tool bar to display further detail information about this particular scheduler.
 */
public class SchedulerScreen extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerScreen.class);
	private static final long serialVersionUID = 1L;
    private MyScheduleUi myScheduleUi;
    private String schedulerSettingsName;
    private HorizontalLayout toolbar;
    private Table table;
    private Button viewJobDetailsButton;
    private String selectedTriggerKeyName;

    public SchedulerScreen(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initToolbar();
        initJobsTable();
    }

    private void initToolbar() {
        toolbar = new HorizontalLayout();
        toolbar.addComponent(createScriptConsoleButton());
        toolbar.addComponent(createViewJobDetailButton());
        addComponent(toolbar);
    }

    private Button createScriptConsoleButton() {
        Button button = new Button("ScriptConsole");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ScriptConsoleWindow console = new ScriptConsoleWindow(myScheduleUi, schedulerSettingsName);
                myScheduleUi.addWindow(console);
            }
        });
        return button;
    }

    private Button createViewJobDetailButton() {
        viewJobDetailsButton = new Button("View Details");
        viewJobDetailsButton.setEnabled(false); // Default disable it until user select a job from table
        viewJobDetailsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (selectedTriggerKeyName == null)
                    return;

                TriggerAndJobDetailWindow window = new TriggerAndJobDetailWindow(
                    myScheduleUi, schedulerSettingsName, selectedTriggerKeyName);
                myScheduleUi.addWindow(window);
            }
        });
        return viewJobDetailsButton;
    }

    private void initJobsTable() {
        table = new Table();
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
            Object[] row = new Object[] {
                triggerKeyName,
                jobKey.getName() + "/" + jobKey.getGroup(),
                trigger.getClass().getSimpleName() + "/" + jobDetail.getJobClass().getSimpleName(),
                df.format(trigger.getNextFireTime()),
                (previousFireTime == null) ? "" : df.format(previousFireTime)
            };
            table.addItem(row, triggerKeyName);
        }

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedTriggerKeyName = (String)event.getProperty().getValue();
                if (selectedTriggerKeyName == null)
                    viewJobDetailsButton.setEnabled(false);
                else
                    viewJobDetailsButton.setEnabled(true);
            }
        });

        // Add table to this UI screen
        addComponent(table);
    }
}
