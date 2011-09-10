package myschedule.service;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An abstract Service implementation that provided basic synchronized flags for started 
 * and initialized state. Subclass should use the initService/startService/stopService/destroyService.
 * 
 * <p>The start() will auto init() if it hasn't already done so. Also the destroy() will auto stop()
 * if it already started. 
 *
 * @author Zemian Deng
 */
public abstract class AbstractService implements Service {

	protected AtomicBoolean started = new AtomicBoolean(false);
	protected AtomicBoolean inited = new AtomicBoolean(false);
	
	@Override
	public void init() {
		if (!inited.get()) {
			inited.set(true);
			initService();
		}
	}

	@Override
	public void start() {
		// Init service if it has not done so.
		if (!inited.get()) {
			init();
		}
		// Start service now.
		if (!started.get()) {
			started.set(true);
			startService();
		}
	}

	@Override
	public void stop() {
		if (started.get()) {
			stopService();
			started.set(false);
		}
	}

	@Override
	public void destroy() {
		// Stop service if it is still running.
		if (started.get()) {
			stop();
		}
		// Destroy service now.
		if (inited.get()) {
			destroyService();
			inited.set(false);
		}
	}

	@Override
	public boolean isStarted() {
		return started.get();
	}

	@Override
	public boolean isInited() {
		return inited.get();
	}

	protected void initService() {}
	protected void startService() {}
	protected void stopService() {}
	protected void destroyService() {}

}
