package myschedule.service;

import static myschedule.service.ErrorCode.SCRIPTING_PROBLEM;
import groovy.lang.GroovyShell;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Provide Groovy scripting service to the scheduler.
 *
 * @author Zemian Deng
 */
public class GroovyScriptingService extends AbstractService implements ScriptingService {

	private static final Logger logger = LoggerFactory.getLogger(GroovyScriptingService.class);
	private static final String SCHEDULER_IMPORTS = "import org.quartz.*\n" +
			"import org.quartz.jobs.*\n" +
			"import myschedule.job.*\n" +
			"import myschedule.job.sample.*\n\n";
	protected boolean autoImportSchedulerPackage = true;
	
	public void setAutoImportSchedulerPackage(boolean autoImportSchedulerPackage) {
		this.autoImportSchedulerPackage = autoImportSchedulerPackage;
	}
	
	@Override
	public <T> T run(String scriptText, Map<String, Object> variables) {
		if (autoImportSchedulerPackage)
			scriptText = SCHEDULER_IMPORTS + scriptText;

		logger.debug("Input scriptText={}", scriptText);
		logger.debug("Binding variables={}", variables);
		
		GroovyShell groovyShell = new GroovyShell();
		for (Map.Entry<String, Object> entry : variables.entrySet())
			groovyShell.setVariable(entry.getKey(), entry.getValue());
		Object object = groovyShell.evaluate(scriptText);
		
		@SuppressWarnings("unchecked")
		T ret = (T)object;
		return ret;
	}

	@Override
	public <T> T runScript(File file, Map<String, Object> variables) {
		try {
			logger.debug("Script file={}", file.getAbsolutePath());
			logger.debug("Binding variables={}", variables);
		
			GroovyShell groovyShell = new GroovyShell();
			for (Map.Entry<String, Object> entry : variables.entrySet())
				groovyShell.setVariable(entry.getKey(), entry.getValue());
	
			Object object = groovyShell.evaluate(file);
						
			@SuppressWarnings("unchecked")
			T ret = (T)object;
			return ret;
		} catch (Exception e) {
			throw new ErrorCodeException(SCRIPTING_PROBLEM, e);
		}
	}

	@Override
	protected void initService() {
	}

	@Override
	protected void destroyService() {
	}

	@Override
	protected void startService() {
	}

	@Override
	protected void stopService() {
	}
	
}
