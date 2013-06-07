package myschedule.web.ui;

import com.vaadin.ui.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        initEditorControls();
    }

    private void initEditorControls() {
        setCaption("Editing Scheduler Config ID: " + schedulerSettingsName);

        String editText = mySchedule.getSchedulerSettingsConfig(schedulerSettingsName);
        editor.setValue(editText);

        Button button = new Button("Save and Restart Scheduler");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String configText = editor.getValue();
                LOGGER.debug("Updating scheduler settings {}.", schedulerSettingsName);
                mySchedule.updateSchedulerSettings(schedulerSettingsName, configText);
                Exception problem = mySchedule.getSchedulerSettings(schedulerSettingsName).getSchedulerException();
                if (problem != null) {
                    myScheduleUi.addWindow(new ErrorWindow(problem));
                } else {
                    close(); // This is a popup, so close it self upon completion.
                }
                myScheduleUi.loadDashboardScreen(); // Now refresh the dashboard for the updated scheduler.
            }
        });
        content.addComponent(button);
    }
}
