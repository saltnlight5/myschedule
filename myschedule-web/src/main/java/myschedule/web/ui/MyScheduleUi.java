package myschedule.web.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * The MySchedule UI application entry point.
 */
public class MyScheduleUi extends UI {
    private static final long serialVersionUID = 1L;
    private VerticalLayout content;

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        getPage().setTitle("MySchedule - UI Manager for Quartz Scheduler");

        content = new VerticalLayout();
        setContent(content);

        content.setSizeFull();
        content.addComponent(new Dashboard());
    }
}
