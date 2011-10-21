package myschedule.web.servlet.app.handler;

import lombok.Getter;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewDataActionHandler;

public class ScriptingHandlers {
	@Getter
	protected ActionHandler runHandler = new ViewDataActionHandler();
}
