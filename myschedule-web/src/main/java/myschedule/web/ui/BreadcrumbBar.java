package myschedule.web.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * A navigation bar for the main application.
 */
public class BreadcrumbBar extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
    private MyScheduleUi myScheduleUi;
    private Button schedulerCrumb;
    private Button scriptingConsole;

    public BreadcrumbBar(MyScheduleUi myScheduleUi) {
        this.myScheduleUi = myScheduleUi;

        Button dashboardButton = new Button("Schedulers Dashboard");
        dashboardButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                BreadcrumbBar.this.myScheduleUi.loadDashboardScreen();
            }
        });
        addComponent(dashboardButton);
    }

    public void addSchedulerCrumb(String schedulerFullName, final String schedulerSettingsName) {
        if (schedulerCrumb != null)
            removeSchedulerCrumb();

        schedulerCrumb = new Button(schedulerFullName);
        schedulerCrumb.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                BreadcrumbBar.this.myScheduleUi.loadSchedulerScreen(schedulerSettingsName);
            }
        });
        addComponent(schedulerCrumb);

        scriptingConsole = new Button("Scripting Console");
        scriptingConsole.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                BreadcrumbBar.this.myScheduleUi.loadScriptingConsoleScreen(schedulerSettingsName);
            }
        });
        addComponent(scriptingConsole);
    }

    public void removeSchedulerCrumb() {
        if (schedulerCrumb == null)
            return;

        removeComponent(scriptingConsole);
        removeComponent(schedulerCrumb);
        scriptingConsole = null;
        schedulerCrumb = null;
    }
}
