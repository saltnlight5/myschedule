package myschedule.web.ui;

import com.vaadin.ui.Button;
import myschedule.web.SchedulerSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A popup UI window to display a text editor to create a scheduler by a Quartz config properties content.
 */
public class EditSchedulerWindow extends EditorWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditSchedulerWindow.class);
	private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;

    public EditSchedulerWindow(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initControls();
    }

    private void initControls() {
        setCaption("Editing Scheduler Config ID: " + schedulerSettingsName);

        // Load default text
        SchedulerSettings settings = mySchedule.getSchedulerSettings(schedulerSettingsName);
        String settingsUrl = settings.getSettingsUrl();
        String editText = mySchedule.getSchedulerSettingsConfig(settingsUrl);
        editor.setValue(editText);

        Button button = new Button("Save and Restart Scheduler");
        button.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                String configText = editor.getValue();
                updateScheduler(configText);
            }
        });
        content.addComponent(button);
    }

    private void updateScheduler(String propsString) {
        LOGGER.debug("Updating scheduler settings {}.", schedulerSettingsName);
        mySchedule.updateSchedulerSettings(schedulerSettingsName, propsString);
        close(); // This is a popup, so close it self upon completion.
        myScheduleUi.loadDashboardScreen(); // Now refresh the dashboard for the newly create scheduler to show.
    }
}
