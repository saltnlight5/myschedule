package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A base class to provide a Window that contains a text editor subclass may extend to provide more controls components.
 * This class
 */
public class EditorWindow extends AbstractWindow {
    TextArea editor;

    public EditorWindow() {
        editor = new TextArea();
        editor.setSizeFull();
        editor.setRows(25);
        content.addComponent(editor);
    }
}
