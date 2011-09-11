package myschedule.service;

public class ExceptionHolder {
	protected Exception exception;
	
	public boolean hasException() {
		return exception != null;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
