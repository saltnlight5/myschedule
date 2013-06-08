package myschedule.web.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import myschedule.web.MySchedule;

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
    private HorizontalLayout currentScreenWrapper;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Setup main page
        getPage().setTitle("MySchedule - Quartz Scheduler Manager");

        // Create components
        content = new VerticalLayout();
        breadcrumbBar = new BreadcrumbBar(this);
        currentScreen = new DashboardScreen(this);

        HorizontalLayout headerContent = createHeader();
        HorizontalLayout footerContent = creatFooter();

        // We needed a wrapper to remain in place so when refreshing currentScreen, the footer won't get messed up.
        currentScreenWrapper = new HorizontalLayout();
        currentScreenWrapper.setSizeFull();
        currentScreenWrapper.addComponent(currentScreen);

        // Setup content
        content.setImmediate(true);
        content.setMargin(true);
        content.addComponent(headerContent);
        content.setComponentAlignment(headerContent, Alignment.MIDDLE_CENTER);
        content.addComponent(breadcrumbBar);
        content.addComponent(currentScreenWrapper);
        content.addComponent(footerContent);
        content.setComponentAlignment(footerContent, Alignment.BOTTOM_RIGHT);
        setContent(content);
    }

    private HorizontalLayout createHeader() {
        Label headerLabel = new Label("<h1>MySchedule - Quartz Scheduler Manager</h1>", ContentMode.HTML);

        HorizontalLayout result = new HorizontalLayout();
        result.addComponent(headerLabel);
        return result;
    }

    private HorizontalLayout creatFooter() {
        String myScheduleAppName = "myschedule";
        MySchedule mySchedule = MySchedule.getInstance();
        String version = mySchedule.getMyScheduleVersion();
        if (!version.equals(""))
            myScheduleAppName += "-" + version;

        String quartzAppName = "quartz";
        version = mySchedule.getQuartzVersion();
        if (!version.equals(""))
            quartzAppName += "-" + version;

        String poweredByText = "Powered by " + myScheduleAppName + " with " + quartzAppName;
        Label poweredByLabel = new Label(poweredByText, ContentMode.PREFORMATTED);

        HorizontalLayout result = new HorizontalLayout();
        result.addComponent(poweredByLabel);
        return result;
    }

    void loadSchedulerScreen(String schedulerSettingsName) {
        currentScreenWrapper.removeComponent(currentScreen);
        currentScreen = new SchedulerScreen(this, schedulerSettingsName);
        currentScreenWrapper.addComponent(currentScreen);

        MySchedule mySchedule = MySchedule.getInstance();
        String schedulerFullName = mySchedule.getSchedulerSettings(schedulerSettingsName).getSchedulerFullName();
        breadcrumbBar.addSchedulerCrumb(schedulerFullName, schedulerSettingsName);
    }

    void loadDashboardScreen() {
        currentScreenWrapper.removeComponent(currentScreen);
        currentScreen = new DashboardScreen(this);
        currentScreenWrapper.addComponent(currentScreen);

        breadcrumbBar.removeSchedulerCrumb();
    }
}
