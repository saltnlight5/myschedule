package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.SchedulerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

/**
 * A popup UI window to display scripting console text editor to manipulate a scheduler.
 */
public class ScriptConsoleWindow extends EditorWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptConsoleWindow.class);
	private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private VerticalLayout consoleContent;
    private VerticalLayout templatesContent;

    public ScriptConsoleWindow(MyScheduleUi myScheduleUi,  String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initEditorControls();
        initTemplatesList();
    }

    private void initTemplatesList() {
        ListSelect list = new ListSelect("Script Templates");
        templatesContent.addComponent(list);

        list.setNullSelectionAllowed(false);
        List<String> names = mySchedule.getScriptTemplatesStore().getNames();
        for (String name : names) {
            list.addItem(name);
        }

        // On selection value change even handler, let's update the editor content
        list.setImmediate(true);
        list.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String name = (String)event.getProperty().getValue();
                String text = mySchedule.getScriptTemplatesStore().get(name);
                editor.setValue(text);
            }
        });
    }

    @Override
    protected void initContent() {
        super.initContent();
        // Wrap this console content.
        HorizontalLayout scriptConsoleContent = new HorizontalLayout();
        content.addComponent(scriptConsoleContent);

        scriptConsoleContent.setSizeFull();
        consoleContent = new VerticalLayout();
        templatesContent = new VerticalLayout();
        scriptConsoleContent.addComponent(consoleContent);
        scriptConsoleContent.setExpandRatio(consoleContent, 5.0f);
        scriptConsoleContent.addComponent(templatesContent);
        scriptConsoleContent.setExpandRatio(templatesContent, 1.0f);
    }

    @Override
    protected void initEditor() {
        editor = new TextArea();
        editor.setSizeFull();
        editor.setRows(25);
        consoleContent.addComponent(editor);
    }

    private void initEditorControls() {
        SchedulerSettings settings = mySchedule.getSchedulerSettings(schedulerSettingsName);
        setCaption("ScriptConsole for " + settings.getSchedulerFullName());

        HorizontalLayout controls = new HorizontalLayout();
        consoleContent.addComponent(controls);

        Button button = new Button("Execute");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String scriptText = editor.getValue();
                String scriptEngineName = "Groovy";
                runScriptText(scriptText, scriptEngineName);
                myScheduleUi.loadSchedulerScreen(schedulerSettingsName);
            }
        });
        controls.addComponent(button);
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
