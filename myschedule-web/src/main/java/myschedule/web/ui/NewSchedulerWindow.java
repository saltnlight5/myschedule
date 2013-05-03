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
        final ListSelect list = new ListSelect("Config Templates");
        templatesContent.addComponent(list);

        list.setNullSelectionAllowed(false);
        List<String> names = mySchedule.getSchedulerTemplatesStore().getNames();
        for (String name : names) {
            list.addItem(name);
        }

        // On selection value change even handler, let's update the editor content
        list.setImmediate(true);
        list.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String name = (String) event.getProperty().getValue();
                String text = mySchedule.getSchedulerTemplatesStore().get(name);
                editor.setValue(text);
            }
        });

        // Save as ... - save content of editor as new template.
        Button saveAsButton = new Button("Save as ...");
        templatesContent.addComponent(saveAsButton);
        saveAsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Prompt to get a name.
                String name = "test2.js"; // just a test for now.

                // Save it.
                String configText = editor.getValue();
                LOGGER.debug("Saving editor content as new template: " + name);
                mySchedule.getSchedulerTemplatesStore().add(name, configText);

                // Show it on template list
                list.addItem(name);
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

        Button button = new Button("Create Scheduler");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String configText = editor.getValue();

                LOGGER.debug("Creating new scheduler settings.");
                mySchedule.addSchedulerSettings(configText);

                close(); // This is a popup, so close it self upon completion.
                myScheduleUi.loadDashboardScreen(); // Now refresh the dashboard for the new scheduler.
            }
        });
        content.addComponent(button);
    }
}
