package myschedule.web.ui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * A popup window for displaying error message, especially those long stacktrace text in a nice scroll text.
 */
public class ErrorWindow extends Window {
    public ErrorWindow(Throwable problem) {
        this(ExceptionUtils.getStackTrace(problem));
    }

    public ErrorWindow(String errorMsg) {
        setCaption("Error");

        // Give default Window position and size
        setWidth("50%");
        setHeight("50%");
        center();

        // Prepare content container
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        setContent(content);

        // Set error text
        content.addComponent(new Label(errorMsg, ContentMode.PREFORMATTED));
    }
}
