package myschedule.service;

public class ErrorCodeException extends RuntimeException {

	/** serialVersionUID - long */
	private static final long serialVersionUID = 1L;
	
	private ErrorCode errorCode = ErrorCode.GENERAL_PROBLEM;
	
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
		this.errorCode = errorCode;
	}

	public ErrorCodeException(ErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}
	
	public ErrorCodeException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	@Override
	public String getMessage() {
		return errorCode + ": " + super.getMessage();
	}
}
