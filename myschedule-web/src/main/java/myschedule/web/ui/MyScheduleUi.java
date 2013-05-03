package myschedule.web.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;
import myschedule.web.SchedulerStatus;

/**
 * The MySchedule UI application entry point. This UI holds the main application and reference to top level UI
 * components. It also expose methods to change and load different UI screens based on user's actions. The top level
 * components of this application would only consist of a bread crumb like navigation bar and a user screen that
 * can be changed/loaded depending on user's action. The default user screen is the DashboardScreen view.
 */
public class MyScheduleUi extends UI {
    private static final long serialVersionUID = 1L;
    private VerticalLayout content;
    private BreadcrumbBar breadcrumbBar;
    private Component currentScreen;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Setup main page
        getPage().setTitle("MySchedule - UI Manager for Quartz Scheduler");

        // Create components
        content = new VerticalLayout();
        breadcrumbBar = new BreadcrumbBar(this);
        currentScreen = new DashboardScreen(this);

        // Setup content
        content.setImmediate(true);
        content.setMargin(true);
        content.addComponent(breadcrumbBar);
        content.addComponent(currentScreen);
        setContent(content);
    }

    void loadSchedulerScreen(String schedulerSettingsName) {
        content.removeComponent(currentScreen);
        currentScreen = new SchedulerScreen(this, schedulerSettingsName);
        content.addComponent(currentScreen);

        MySchedule mySchedule = MySchedule.getInstance();
        String schedulerFullName = mySchedule.getSchedulerSettings(schedulerSettingsName).getSchedulerFullName();
        SchedulerTemplate scheduler = mySchedule.getScheduler(schedulerSettingsName);
        SchedulerStatus status = MySchedule.getSchedulerStatus(scheduler);
        breadcrumbBar.addSchedulerCrumb(schedulerFullName, schedulerSettingsName, status.toString());
    }

    void loadDashboardScreen() {
        content.removeComponent(currentScreen);
        currentScreen = new DashboardScreen(this);
        content.addComponent(currentScreen);

        breadcrumbBar.removeSchedulerCrumb();
    }
}
