package myschedule.web.ui;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**s
 * This content view shows Quartz calendar objects that are mainly used for date exclusions on triggers/jobs.
 *
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class CalendarsContent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarsContent.class);
    private String schedulerSettingsName;
    private MySchedule mySchedule = MySchedule.getInstance();

    public CalendarsContent(String schedulerSettingsName) {
        this.schedulerSettingsName = schedulerSettingsName;
        initCalendarsTable();
    }

    void initCalendarsTable() {
        Table table = new Table("Quartz Calendar Objects (mainly for exclusion of job schedules)");
        addComponent(table);

        table.setSizeFull();

        Object defaultValue = null; // Not used.
        table.addContainerProperty("Name", String.class, defaultValue);
        table.addContainerProperty("Class", String.class, defaultValue);
        table.addContainerProperty("Description", String.class, defaultValue);
        table.addContainerProperty("Base Calendar", String.class, defaultValue);
        table.addContainerProperty("Extra Info", String.class, defaultValue);

        // Fill table data
        LOGGER.debug("Loading calendars status table for %s", schedulerSettingsName);
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        String[] calendarNames = scheduler.getCalendarNames();

        int index = 1;
        for (String name : calendarNames) {
            Calendar calendar = scheduler.getCalendar(name);
            Object[] row = new Object[]{
                    name,
                    calendar.getClass().getName(),
                    calendar.getDescription(),
                    calendar.getBaseCalendar(),
                    calendar.toString()};
            table.addItem(row, index++);
        }
    }
}