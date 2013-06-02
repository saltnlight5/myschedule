package myschedule.web.ui;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**s
 * This content view shows the scheduler status and runtime information.
 *
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class SchedulerStatusContent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerStatusContent.class);
    private String schedulerSettingsName;
    private MySchedule mySchedule = MySchedule.getInstance();
    private Map<String, String> pluginClassNames;

    public SchedulerStatusContent(String schedulerSettingsName) {
        this.schedulerSettingsName = schedulerSettingsName;
        initSchedulerStatusTable();
        initListenersInfoTable();
        initPluginsInfoTable();
    }

    void initSchedulerStatusTable() {
        Table table = new Table("Scheduler Status and Runtime Information");
        addComponent(table);

        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Property Name", String.class, defaultValue);
        table.addContainerProperty("Property Value", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading scheduler status table for %s", schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        SchedulerMetaData schedulerMetaData = scheduler.getSchedulerMetaData();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 1;
        addTableItem(table, index++, "Scheduler Name", "" + schedulerMetaData.getSchedulerName());
        addTableItem(table, index++, "Scheduler Instance Id", "" + schedulerMetaData.getSchedulerInstanceId());
        addTableItem(table, index++, "Version", "" + schedulerMetaData.getVersion());
        addTableItem(table, index++, "Scheduler Class", "" + schedulerMetaData.getSchedulerClass());
        addTableItem(table, index++, "In Standby Mode", "" + schedulerMetaData.isInStandbyMode());
        addTableItem(table, index++, "Shutdown", "" + schedulerMetaData.isShutdown());
        addTableItem(table, index++, "Started", "" + schedulerMetaData.isStarted());
        addTableItem(table, index++, "Running Since", "" + toDateStr(schedulerMetaData.getRunningSince(), df));
        addTableItem(table, index++, "Number of Jobs Executed", "" + schedulerMetaData.getNumberOfJobsExecuted());
        addTableItem(table, index++, "Thread Pool Class", "" + schedulerMetaData.getThreadPoolClass());
        addTableItem(table, index++, "Thread Pool Size", "" + schedulerMetaData.getThreadPoolSize());
        addTableItem(table, index++, "JobStore Class", "" + schedulerMetaData.getJobStoreClass());
        addTableItem(table, index++, "JobStore Clustered", "" + schedulerMetaData.isJobStoreClustered());
        addTableItem(table, index++, "JobStore Supports Persistence", "" + schedulerMetaData.isJobStoreSupportsPersistence());
        addTableItem(table, index++, "Scheduler Remote", "" + schedulerMetaData.isSchedulerRemote());
    }

    void initListenersInfoTable() {
        Table table = new Table("Listeners Information");
        addComponent(table);

        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Type", String.class, defaultValue);
        table.addContainerProperty("Class", String.class, defaultValue);
        table.addContainerProperty("Info", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading listeners information table for %s", schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        ListenerManager listenerManager = scheduler.getListenerManager();
        int index = 1;
        for (SchedulerListener listener : listenerManager.getSchedulerListeners()) {
            addTableItem(table, index++, "Scheduler Listener", listener.getClass().getName(), listener.toString());
        }
        for (TriggerListener listener : listenerManager.getTriggerListeners()) {
            addTableItem(table, index++, "Trigger Listener", listener.getClass().getName(), listener.toString());
        }
        for (JobListener listener : listenerManager.getJobListeners()) {
            addTableItem(table, index++, "Job Listener", listener.getClass().getName(), listener.toString());
        }
    }


    void initPluginsInfoTable() {
        Table table = new Table("Plugins Information");
        addComponent(table);

        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Name", String.class, defaultValue);
        table.addContainerProperty("Class", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading plugins information table for %s", schedulerSettingsName);
        Map<String, String> nameClassMap = getPluginClassNames();
        int index = 1;
        for (String name : nameClassMap.keySet()) {
            addTableItem(table, index++, name, nameClassMap.get(name));
        }
    }

    private Map<String, String> getPluginClassNames() {
        if (pluginClassNames == null) {
            pluginClassNames = new HashMap<String, String>();
            Properties props = mySchedule.getSchedulerSettings(schedulerSettingsName).getQuartzProperties();
            String pluginPrefix = "org.quartz.plugin.";
            int index = 1;
            for (String name : props.stringPropertyNames()) {
                if (!name.startsWith(pluginPrefix))
                    continue;

                String pluginName = name.substring(pluginPrefix.length());
                int pos = pluginName.indexOf(".");
                if (pos <= 0)
                    continue;

                pluginName = pluginName.substring(0, pos);
                if (pluginClassNames.containsKey(pluginName))
                    continue;

                String pluginClass = props.getProperty(pluginPrefix + pluginName + ".class");
                if (pluginClass == null)
                    continue;

                pluginClassNames.put(pluginName, pluginClass);
            }
        }

        return pluginClassNames;
    }

    private String toDateStr(Date date, SimpleDateFormat df) {
        if (date == null)
            return "";
        else
            return df.format(date);
    }

    private void addTableItem(Table table, int itemId, String name, String value) {
        Object[] row = new Object[]{name, value};
        table.addItem(row, itemId);
    }

    private void addTableItem(Table table, int itemId, String name, String value1, String value2) {
        Object[] row = new Object[]{name, value1, value2};
        table.addItem(row, itemId);
    }
}