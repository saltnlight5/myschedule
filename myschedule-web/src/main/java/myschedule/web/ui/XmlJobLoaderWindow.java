package myschedule.web.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import myschedule.quartz.extra.QuartzExtraUtils;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.util.ScriptingUtils;
import myschedule.quartz.extra.util.Utils;
import myschedule.web.SchedulerSettings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * A popup UI window to display a text editor to load jobs via Quartz's Xml plugin.
 */
public class XmlJobLoaderWindow extends EditorWindow {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlJobLoaderWindow.class);
    private static final long serialVersionUID = 1L;
    private String schedulerSettingsName;
    private VerticalLayout editorContent;
    private VerticalLayout templatesContent;
    private ListSelect templatesList;

    public XmlJobLoaderWindow(MyScheduleUi myScheduleUi, String schedulerSettingsName) {
        this.myScheduleUi = myScheduleUi;
        this.schedulerSettingsName = schedulerSettingsName;
        initEditorControls();
        initTemplatesList();
    }

    /**
     * Init content will be invoked by super constructor before other initXXX() methods.
     */
    @Override
    protected void initContent() {
        super.initContent();

        editorContent = new VerticalLayout();
        templatesContent = new VerticalLayout();

        // Wrap this editor content.
        HorizontalLayout xmlEditorContent = new HorizontalLayout();
        content.addComponent(xmlEditorContent);

        xmlEditorContent.setSizeFull();
        xmlEditorContent.addComponent(editorContent);
        xmlEditorContent.setExpandRatio(editorContent, 5.0f);
        xmlEditorContent.addComponent(templatesContent);
        xmlEditorContent.setExpandRatio(templatesContent, 1.0f);
    }

    private void initTemplatesList() {
        templatesList = new ListSelect("Xml Templates");
        templatesContent.addComponent(templatesList);

        templatesList.setNullSelectionAllowed(false);
        List<String> names = mySchedule.getXmlJobLoaderTemplatesStore().getNames();
        for (String name : names) {
            templatesList.addItem(name);
        }

        // On selection value change even handler, let's update the editor content
        templatesList.setImmediate(true);
        templatesList.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                String name = (String) event.getProperty().getValue();
                String text = mySchedule.getXmlJobLoaderTemplatesStore().get(name);
                editor.setValue(text);
            }
        });
    }

    @Override
    protected void initEditor() {
        editor = new TextArea();
        editor.setSizeFull();
        editor.setRows(25);
        editorContent.addComponent(editor);
    }

    private void initEditorControls() {
        SchedulerSettings settings = mySchedule.getSchedulerSettings(schedulerSettingsName);
        setCaption("Xml Job Loader for scheduler: " + settings.getSchedulerFullName());

        HorizontalLayout controls = new HorizontalLayout();
        editorContent.addComponent(controls);

        // Create the action button.
        Button button = new Button("Load Jobs");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String scriptText = editor.getValue();
                InputStream inStream = new ByteArrayInputStream(scriptText.getBytes());
                try {
                    SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
                    QuartzExtraUtils.scheduleXmlSchedulingData(inStream, scheduler.getScheduler());
                } finally {
                    IOUtils.closeQuietly(inStream);
                }
                myScheduleUi.loadSchedulerScreen(schedulerSettingsName);
            }
        });
        controls.addComponent(button);

        // Save as ... button - save content of editor as new template.
        button = new Button("Save Xml as Template ...");
        controls.addComponent(button);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Prompt to get a name.
                InputPromptWindow prompt = new InputPromptWindow("Please enter a xml template name (without extension)",
                        new InputPromptWindow.InputAction() {
                            @Override
                            public void onInputOk(String inputValue) {
                                // Save template content.
                                String name = inputValue;
                                String xmlText = editor.getValue();
                                if (StringUtils.isEmpty(name))
                                    throw new RuntimeException("Template name can not be empty.");
                                LOGGER.debug("Saving editor content as new xml template: " + name);
                                mySchedule.getXmlJobLoaderTemplatesStore().add(name, xmlText);

                                // Show it on template list
                                templatesList.addItem(name);
                            }
                        });
                myScheduleUi.addWindow(prompt);
            }
        });
    }
}
