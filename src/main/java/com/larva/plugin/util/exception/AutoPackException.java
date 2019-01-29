package com.larva.plugin.util.exception;

public class AutoPackException extends RuntimeException{
	
	private static final long serialVersionUID = 2405910304666073055L;

	public AutoPackException() {
		super();
	}

	public AutoPackException(String message, Throwable cause) {
		super(message, cause);
	}

	public AutoPackException(String message) {
		super(message);
	}

	public AutoPackException(Throwable cause) {
		super(cause);
	}

}
