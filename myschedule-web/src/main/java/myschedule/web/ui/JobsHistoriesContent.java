package myschedule.web.ui;

import com.vaadin.ui.*;
import myschedule.quartz.extra.JdbcSchedulerHistoryPlugin;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**s
 * JobsHistoriesContent provide a table view for Job histories recorded by JdbcSchedulerHistoryPlugin.
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class JobsHistoriesContent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsHistoriesContent.class);
    MySchedule mySchedule = MySchedule.getInstance();
    MyScheduleUi myScheduleUi;
    String schedulerSettingsName;
    HorizontalLayout toolbar;
    Table table;

    public JobsHistoriesContent(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initToolbar();
        initJobsTable();
    }

    private void initToolbar() {
        toolbar = new HorizontalLayout();
        addComponent(toolbar);

        toolbar.addComponent(createRefreshButton());
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

    private void initJobsTable() {
        table = new Table();
        addComponent(table);

        table.setSizeFull();
        table.setImmediate(true);
        table.setSelectable(true);

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Host IP/Name", String.class, defaultValue);
        table.addContainerProperty("Scheduler Name", String.class, defaultValue);
        table.addContainerProperty("Event Type", String.class, defaultValue);
        table.addContainerProperty("Event Name", String.class, defaultValue);
        table.addContainerProperty("Event Time", String.class, defaultValue);
        table.addContainerProperty("Info 1", String.class, defaultValue);
        table.addContainerProperty("Info 2", String.class, defaultValue);
        table.addContainerProperty("Info 3", String.class, defaultValue);
        table.addContainerProperty("Info 4", String.class, defaultValue);
        table.addContainerProperty("Info 5", String.class, defaultValue);

        // Fill table data
        reloadTableContent();
    }

    private void reloadTableContent() {
        MySchedule mySchedule = MySchedule.getInstance();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        String key = mySchedule.getMyScheduleSettings().getJdbcSchedulerHistoryPluginContextKey();
        JdbcSchedulerHistoryPlugin plugin = (JdbcSchedulerHistoryPlugin)scheduler.getContext().get(key);
        if (plugin == null) {
            String msg = "No JdbcSchedulerHistoryPlugin detected! Please configure this plugin to record scheduler " +
                    "events and job histories in your scheduler config settings.";
            Notification.show("WARNING", msg, Notification.Type.WARNING_MESSAGE);

            // End this method here.
            return;
        }

        table.removeAllItems();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LOGGER.debug("Loading job hitories from scheduler {}", schedulerSettingsName);
        List<List<Object>> histories = plugin.getJobHistoryData();
        int index = 0;
        for (List<Object> history : histories) {
            Object[] row = new Object[]{
                    history.get(0) + "/" + history.get(1),
                    toStr(history.get(2)),
                    toStr(history.get(3)),
                    toStr(history.get(4)),
                    (history.get(5) instanceof Date) ?
                        toDateStr((Date)history.get(5), df) :
                        toStr(history.get(5)),
                    toStr(history.get(6)),
                    toStr(history.get(7)),
                    toStr(history.get(8)),
                    toStr(history.get(9)),
                    toStr(history.get(10))
            };
            table.addItem(row, index++);
        }
    }

    private String toStr(Object item) {
        if (item == null)
            return "";
        else
            return "" + item;
    }

    private String toDateStr(Date date, SimpleDateFormat df) {
        if (date == null)
            return "";
        else
            return df.format(date);
    }
}