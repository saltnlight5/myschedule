package myschedule.web.ui;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * A base class to provide a popup UI window on an existing screen. It setup screen to fill up 90% by 90% on center of
 * the screen. It also provide a default vertical layout for subclass to add content components.
 */
public class AbstractWindow extends Window {
    protected VerticalLayout content;

    public AbstractWindow() {
        setWidth("90%");
        setHeight("90%");
        center();

        content = new VerticalLayout();
        setContent(content);
    }
}
