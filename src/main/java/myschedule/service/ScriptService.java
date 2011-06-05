package myschedule.service;

import groovy.lang.GroovyShell;

/** 
 * GroovyScriptService
 *
 * @author Zemian Deng
 */
public class ScriptService {

	private GroovyShell groovyShell = new GroovyShell();

	public <T> T run(String scriptText) {
		Object object = groovyShell.evaluate(scriptText);
		@SuppressWarnings("unchecked")
		T ret = (T)object;
		return ret;
	}
	
}
