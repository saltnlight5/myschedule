package myschedule.web.ui;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import myschedule.web.MySchedule;

/**
 * A base class to provide a popup UI window on an existing screen. It setup screen to fill up 90% by 90% on center of
 * the screen. It also provide a default vertical layout for subclass to add content components.
 *
 * This base class also provide and setup MySchedule and MyScheduleUi access.
 */
public class AbstractWindow extends Window {
    protected MySchedule mySchedule = MySchedule.getInstance();
    protected VerticalLayout content;
    protected MyScheduleUi myScheduleUi;

    public AbstractWindow() {
        // Give default Window position and size
        setWidth("80%");
        setHeight("98%");
        center();

        // Prepare content container
        content = new VerticalLayout();
        content.setMargin(true);
        setContent(content);
    }
}
