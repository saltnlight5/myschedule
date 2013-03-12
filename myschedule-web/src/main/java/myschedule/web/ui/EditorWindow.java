package myschedule.web.ui;

import com.vaadin.ui.TextArea;

/**
 * A base class to provide a Window that contains a text editor subclass may extend to provide more controls components.
 * This class
 */
public class EditorWindow extends AbstractWindow {
    TextArea editor;

    public EditorWindow() {
        initEditor();
    }

    protected void initEditor() {
        editor = new TextArea();
        editor.setSizeFull();
        editor.setRows(25);
        content.addComponent(editor);
    }
}
