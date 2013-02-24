package myschedule.web.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * A Vaadin UI application entry point.
 */
public class MyScheduleUI extends UI {
    private static final long serialVersionUID = 1L;
    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        addWindow(new MainWindow());
    }
}
