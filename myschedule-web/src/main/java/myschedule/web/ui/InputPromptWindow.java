package myschedule.web.ui;

import com.vaadin.ui.*;

/**
 * A prompt window for user inputs.
 */
public class InputPromptWindow extends Window {
    private String inputLabel;
    private TextField input = new TextField();
    private InputAction inputAction;

    public InputPromptWindow(String inputLabel, InputAction inputAction) {
        this.inputLabel = inputLabel;
        this.inputAction = inputAction;
        initContent();
    }

    protected void initContent() {
        // Give default Window position and size
        setWidth("400px");
        setHeight("200px");
        center();

        // adjust input width
        input.setWidth("300px"); // TODO: how to auto fill the width?

        // Prepare content container
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        setContent(content);

        // Fill up content
        content.addComponent(new Label(inputLabel));
        content.addComponent(input);

        Button button = new Button("OK");
        content.addComponent(button);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String inputValue = getInputValue();
                inputAction.onInputOk(inputValue);
                close();
            }
        });
    }

    public String getInputValue() {
        return input.getValue();
    }

    public static interface InputAction {
        void onInputOk(String inputValue);
    }
}
