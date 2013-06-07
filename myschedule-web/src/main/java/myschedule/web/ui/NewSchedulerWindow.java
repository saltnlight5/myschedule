package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import myschedule.web.SchedulerSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A popup UI window to display a text editor to create a scheduler by a Quartz config properties content.
 */
public class NewSchedulerWindow extends EditorWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewSchedulerWindow.class);
    private static final long serialVersionUID = 1L;
    private VerticalLayout consoleContent;
    private VerticalLayout templatesContent;
    private ListSelect templatesList;

    public NewSchedulerWindow(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;
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
        templatesList = new ListSelect("Config Templates");
        templatesContent.addComponent(templatesList);

        templatesList.setNullSelectionAllowed(false);
        List<String> names = mySchedule.getSchedulerTemplatesStore().getNames();
        for (String name : names) {
            templatesList.addItem(name);
        }

        // On selection value change even handler, let's update the editor content
        templatesList.setImmediate(true);
        templatesList.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String name = (String) event.getProperty().getValue();
                String text = mySchedule.getSchedulerTemplatesStore().get(name);
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
        setCaption("New Quartz Scheduler Configuration Properties");

        // Load default text
        String defaultText = mySchedule.getDefaultSchedulerSettingsConfigText();
        if (StringUtils.isNotEmpty(defaultText)) {
            // Try to make default scheduler name unique
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String newName = "MyQuartzScheduler-" + df.format(new Date());
            defaultText = defaultText.replace(SchedulerSettings.DEFAULT_SCHEDULER_NAME, newName);
            editor.setValue(defaultText);
        }

        HorizontalLayout controls = new HorizontalLayout();
        content.addComponent(controls);

        // Create new scheduler button
        Button button = new Button("Create Scheduler");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String configText = editor.getValue();

                LOGGER.debug("Creating new scheduler settings.");
                SchedulerSettings settings = null;
                try {
                    settings = mySchedule.addSchedulerSettings(configText);
                    close(); // This is a popup, so close it self upon completion.
                } catch (Exception e) {
                    myScheduleUi.addWindow(new ErrorWindow(e));
                }
                myScheduleUi.loadDashboardScreen(); // Now refresh the dashboard for the updated scheduler.
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
                InputPromptWindow prompt = new InputPromptWindow("Please enter a config template name",
                        new InputPromptWindow.InputAction() {
                            @Override
                            public void onInputOk(String inputValue) {
                                // Save template content.
                                String name = inputValue;
                                String configText = editor.getValue();
                                if (StringUtils.isEmpty(name))
                                    throw new RuntimeException("Template name can not be empty.");
                                LOGGER.debug("Saving editor content as new config template: " + name);
                                mySchedule.getSchedulerTemplatesStore().add(name, configText);

                                // Show it on template list
                                templatesList.addItem(name);
                            }
                        });
                myScheduleUi.addWindow(prompt);
            }
        });
    }
}
