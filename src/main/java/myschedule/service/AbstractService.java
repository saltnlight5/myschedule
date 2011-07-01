package myschedule.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract Service that provide logger, and state management for start/stop and 
 * init/destroy implementation.
 *
 * @author Zemian Deng
 */
public abstract class AbstractService implements Service {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected AtomicBoolean started = new AtomicBoolean(false);
	protected AtomicBoolean initialized = new AtomicBoolean(false);
	
	@Override
	public void init() {
		if (!isInitialized()) {
			initService();
			initialized.set(true);
			logger.info("Service " + this + " initialized.");
		}
	}

	@Override
	public void destroy() {
		if (isInitialized()) {
			destroyService();
			initialized.set(false);
			logger.info("Service " + this + " destroyed.");
		}
	}

	@Override
	public void start() {
		if (!isStarted()) {
			startService();
			started.set(true);
			logger.info("Service " + this + " started.");
		}
	}

	@Override
	public void stop() {
		if (isStarted()) {
			stopService();
			started.set(false);
			logger.info("Service " + this + " stopped.");
		}
	}

	@Override
	public boolean isStarted() {
		return started.get();
	}

	@Override
	public boolean isInitialized() {
		return initialized.get();
	}

	abstract protected void initService();

	abstract protected void destroyService();

	abstract protected void startService();

	abstract protected void stopService();

}
