package myschedule.service;

import java.io.File;

import groovy.lang.GroovyShell;

/** 
 * Provide Groovy scripting service to the scheduler.
 *
 * @author Zemian Deng
 */
public class ScriptingService {

	protected GroovyShell groovyShell = new GroovyShell();

	public <T> T run(String scriptText) {
		Object object = groovyShell.evaluate(scriptText);
		@SuppressWarnings("unchecked")
		T ret = (T)object;
		return ret;
	}

	public <T> T runScript(File file) {
		try {
			Object object = groovyShell.evaluate(file);
			@SuppressWarnings("unchecked")
			T ret = (T)object;
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
