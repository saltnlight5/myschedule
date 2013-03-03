package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A popup UI window to display a text editor to create a scheduler by a Quartz config properties content.
 */
public class NewSchedulerWindow extends EditorWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewSchedulerWindow.class);
	private static final long serialVersionUID = 1L;

    public NewSchedulerWindow(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;
        initControls();
    }

    private void initControls() {
        setCaption("Creating a new Scheduler with Quartz configuration properties");

        // Load default text
        String defaultText = mySchedule.getUserDefaultSchedulerConfig();
        if (StringUtils.isNotEmpty(defaultText)) {
            // Try to make default scheduler name unique
            SimpleDateFormat df = new SimpleDateFormat("YYYYmmdd-HHmmss");
            String newName = SchedulerSettings.DEFAULT_SCHEDULER_NAME + df.format(new Date());
            defaultText = defaultText.replace(SchedulerSettings.DEFAULT_SCHEDULER_NAME, newName);
            editor.setValue(defaultText);
        }

        Button button = new Button("Create");
        button.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                String configText = editor.getValue();
                createScheduler(configText);
            }
        });
        content.addComponent(button);
    }

    private void createScheduler(String propsString) {
        LOGGER.debug("Creating new scheduler settings.");
        mySchedule.addSchedulerSettings(propsString);
        close(); // This is a popup, so close it self upon completion.
        myScheduleUi.loadDashboardScreen(); // Now refresh the dashboard for the newly create scheduler to show.
    }
}
