package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.util.ScriptingUtils;
import myschedule.quartz.extra.util.Utils;
import myschedule.web.SchedulerSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * A popup UI window to display scripting console text editor to manipulate a scheduler.
 */
public class ScriptConsoleWindow extends EditorWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptConsoleWindow.class);
    private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private VerticalLayout consoleContent;
    private VerticalLayout templatesContent;
    private ComboBox scriptEngineList;
    private List<String> scriptEngineNames;
    private String defaultScriptEngineName;
    private ListSelect templatesList;

    public ScriptConsoleWindow(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;

        this.scriptEngineNames = ScriptingUtils.getAllScriptEngineNames();
        this.defaultScriptEngineName = mySchedule.getMyScheduleSettings().getDefaultScriptEngineName();

        initEditorControls();
        initTemplatesList();
    }

    /**
     * Init content will be invoked by super constructor before other initXXX() methods.
     */
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

    private void initTemplatesList() {
        templatesList = new ListSelect("Script Templates");
        templatesContent.addComponent(templatesList);

        templatesList.setNullSelectionAllowed(false);
        List<String> names = mySchedule.getScriptTemplatesStore().getNames();
        for (String name : names) {
            templatesList.addItem(name);
        }

        // On selection value change even handler, let's update the editor content
        templatesList.setImmediate(true);
        templatesList.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String name = (String) event.getProperty().getValue();
                String text = mySchedule.getScriptTemplatesStore().get(name);
                editor.setValue(text);
            }
        });
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

        // Create script engine option list that default to Groovy if found.
        scriptEngineList = new ComboBox();
        scriptEngineList.setNullSelectionAllowed(false);
        for (String engineName : scriptEngineNames)
            scriptEngineList.addItem(engineName);
        if (scriptEngineNames.size() > 0) {
            if (scriptEngineNames.contains(defaultScriptEngineName))
                scriptEngineList.setValue(defaultScriptEngineName);
            else
                scriptEngineList.setValue(scriptEngineNames.get(0));
        }
        controls.addComponent(new Label("Scripting Engine: "));
        controls.addComponent(scriptEngineList);

        // Create the run script button.
        Button button = new Button("Run Script");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String scriptText = editor.getValue();
                String scriptEngineName = (String) scriptEngineList.getValue();
                runScriptText(scriptEngineName, scriptText);
                myScheduleUi.loadSchedulerScreen(schedulerSettingsName);
            }
        });
        controls.addComponent(button);

        // Save as ... button - save content of editor as new template.
        button = new Button("Save Config as Template ...");
        controls.addComponent(button);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Prompt to get a name.
                InputPromptWindow prompt = new InputPromptWindow("Please enter a script template name (with extension)",
                        new InputPromptWindow.InputAction() {
                            @Override
                            public void onInputOk(String inputValue) {
                                // Save template content.
                                String name = inputValue;
                                String configText = editor.getValue();
                                if (StringUtils.isEmpty(name))
                                    throw new RuntimeException("Template name can not be empty.");
                                LOGGER.debug("Saving editor content as new script template: " + name);
                                mySchedule.getScriptTemplatesStore().add(name, configText);

                                // Show it on template list
                                templatesList.addItem(name);
                            }
                        });
                myScheduleUi.addWindow(prompt);
            }
        });
    }

    private void runScriptText(String scriptEngineName, String scriptText) {
        // Bind scheduler as implicit variable
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        Map<String, Object> bindings = Utils.toMap("scheduler", scheduler);
        ScriptingUtils.runScriptText(scriptEngineName, scriptText, bindings);
    }
}
