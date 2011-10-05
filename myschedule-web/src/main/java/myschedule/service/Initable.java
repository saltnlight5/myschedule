package myschedule.service;

/**
 * 
 * A service that provide init/destroy interface.
 *
 * <p>
 * Use {@link ServiceContainer} to auto manage the lifecycles.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public interface Initable {
	void init();
	void destroy();
	boolean isInited();
}
