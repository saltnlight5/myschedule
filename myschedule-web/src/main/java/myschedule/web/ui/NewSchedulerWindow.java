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

    private void initTemplatesList() {
        ListSelect list = new ListSelect("Config Templates");
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
                String name = (String)event.getProperty().getValue();
                String text = mySchedule.getSchedulerTemplatesStore().get(name);
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
