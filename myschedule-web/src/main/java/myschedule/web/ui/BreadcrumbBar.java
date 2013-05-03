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
    private Label schedulerStatusCrumb;

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

    public void addSchedulerCrumb(String schedulerFullName, final String schedulerSettingsName, String status) {
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

        schedulerStatusCrumb = new Label("Status: " + status);
        addComponent(schedulerStatusCrumb);
    }

    public void removeSchedulerCrumb() {
        if (schedulerCrumb == null)
            return;

        removeComponent(schedulerCrumb);
        schedulerCrumb = null;

        removeComponent(schedulerStatusCrumb);
        schedulerStatusCrumb = null;
    }
}
