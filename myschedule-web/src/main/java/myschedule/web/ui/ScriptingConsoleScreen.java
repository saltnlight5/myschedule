package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UI screen that display scripting console text editor to manipulate a scheduler.
 */
public class ScriptingConsoleScreen extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptingConsoleScreen.class);
	private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private TextArea editor;

    public ScriptingConsoleScreen(String schedulerSettingsName) {
        this.schedulerSettingsName = schedulerSettingsName;

        initEditor();
    }

    private void initEditor() {
        editor = new TextArea();
        editor.setSizeFull();
        editor.setRows(25);

        Button button = new Button("Run");
        button.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                String scriptText = editor.getValue();
                String scriptEngineName = "Groovy";
                runScriptText(scriptText, scriptEngineName);
            }
        });

        // Add component to this UI screen
        addComponent(editor);
        addComponent(button);
    }

    private void runScriptText(String scriptText, String scriptEngineName) {
        LOGGER.debug("Evaluating script using engine={}.", scriptEngineName);

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine scriptEngine = factory.getEngineByName(scriptEngineName);
        if (scriptEngine == null) {
            throw new RuntimeException("Script engine=" + scriptEngineName + " is not found.");
        }

        MySchedule mySchedule = MySchedule.getInstance();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);

        // Script engine binding variables.
        Bindings bindings = scriptEngine.createBindings();
        bindings.put("scheduler", scheduler);

        // Evaluate script text.
        try {
            Object result = scriptEngine.eval(scriptText, bindings);
            LOGGER.info("Script evaluated with result={}", result);
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to run " + scriptEngineName + " script.", e);
        }
    }
}
