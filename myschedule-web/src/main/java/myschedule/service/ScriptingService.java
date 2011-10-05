package myschedule.service;

import java.io.File;
import java.util.Map;

public interface ScriptingService extends Service {

	<T> T run(String scriptText, Map<String, Object> variables);

	<T> T runScript(File file, Map<String, Object> variables);

}