package com.webserver.http;

/**
 * 空请求异常
 * 当实例化HttpRequest时,若为空请求时会抛出该异常
 *
 */
public class EmptyRequestException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyRequestException() {
		super();
	}

	public EmptyRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EmptyRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyRequestException(String message) {
		super(message);
	}

	public EmptyRequestException(Throwable cause) {
		super(cause);
	}
	
}
