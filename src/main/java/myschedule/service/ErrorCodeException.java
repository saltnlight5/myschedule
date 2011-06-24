package myschedule.service;

public class ErrorCodeException extends RuntimeException {

	/** serialVersionUID - long */
	protected static final long serialVersionUID = 1L;
	
	protected ErrorCode errorCode;
	
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
	public ErrorCodeException() {
	}

	public ErrorCodeException(String message) {
		super(message);
	}

	public ErrorCodeException(Throwable cause) {
		super(cause);
	}

	public ErrorCodeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ErrorCodeException(ErrorCode errorCode, String message) {
		super(message);
	}

	public ErrorCodeException(ErrorCode errorCode, Throwable cause) {
		super(cause);
	}
	
	public ErrorCodeException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
	}
}
