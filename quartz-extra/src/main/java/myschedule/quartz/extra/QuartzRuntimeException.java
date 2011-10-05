package myschedule.quartz.extra;

import org.quartz.SchedulerException;

public class QuartzRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected SchedulerException schedulerException;
	
	public SchedulerException getSchedulerException() {
		return schedulerException;
	}
	
	public void setSchedulerException(SchedulerException schedulerException) {
		this.schedulerException = schedulerException;
	}
	
	public QuartzRuntimeException(SchedulerException schedulerException) {
		setSchedulerException(schedulerException);
	}
	
	public QuartzRuntimeException() {
	}

	public QuartzRuntimeException(String message) {
		super(message);
	}

	public QuartzRuntimeException(Throwable cause) {
		super(cause);
	}

	public QuartzRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
