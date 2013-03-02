package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A popup UI window to display a text editor to create a scheduler by a Quartz config properties content.
 */
public class NewSchedulerWindow extends AbstractWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewSchedulerWindow.class);
	private static final long serialVersionUID = 1L;
    private TextArea editor;
    private MyScheduleUi myScheduleUi;

    public NewSchedulerWindow(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;
        initEditor();
    }

    private void initEditor() {
        setCaption("Creating a new Scheduler with Quartz configuration properties");

        editor = new TextArea();
        editor.setSizeFull();
        editor.setRows(25);

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

        // Add component to this UI screen
        content.addComponent(editor);
        content.addComponent(button);
    }

    private void createScheduler(String propsString) {
        LOGGER.debug("Creating new scheduler with a config text.");
        MySchedule mySchedule = MySchedule.getInstance();
        mySchedule.addSchedulerSettings(propsString);
        close(); // This is a popup, so close it self upon completion.
        myScheduleUi.loadDashboardScreen(); // Now refresh the dashboard for the newly create scheduler to show.
    }
}
