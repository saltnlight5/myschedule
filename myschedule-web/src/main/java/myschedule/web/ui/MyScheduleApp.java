package myschedule.web.ui;

import com.vaadin.Application;

/**
 * A Vaadin UI application entry point.
 */
public class MyScheduleApp extends Application {	
	private static final long serialVersionUID = 1L;
	
	@Override
    public void init() {
        setMainWindow(new MainWindow());
    }
}
