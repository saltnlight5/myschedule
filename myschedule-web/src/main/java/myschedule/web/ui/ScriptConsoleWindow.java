package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
 * UI screen that display scripting console text editor to manipulate a scheduler.
 */
public class ScriptConsoleWindow extends Window {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptConsoleWindow.class);
	private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private TextArea editor;
    private MySchedule mySchedule = MySchedule.getInstance();

    public ScriptConsoleWindow(String schedulerSettingsName) {
        this.schedulerSettingsName = schedulerSettingsName;

        initEditor();
    }

    private void initEditor() {
        SchedulerSettings settings = mySchedule.getSchedulerSettings(schedulerSettingsName);
        setCaption("ScriptConsole for " + settings.getSchedulerFullName());
        setWidth("90%");
        setHeight("90%");
        center();

        VerticalLayout content = new VerticalLayout();
        setContent(content);

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
        content.addComponent(editor);
        content.addComponent(button);
    }

    private void runScriptText(String scriptText, String scriptEngineName) {
        LOGGER.debug("Evaluating script using engine={}.", scriptEngineName);

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine scriptEngine = factory.getEngineByName(scriptEngineName);
        if (scriptEngine == null) {
            throw new RuntimeException("Script engine=" + scriptEngineName + " is not found.");
        }

        // Script engine binding variables.
        Bindings bindings = scriptEngine.createBindings();

        // Bind scheduler as implicit variable
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
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
