package com.github.andlyticsproject.console;

public class NetworkException extends DevConsoleException {

/**
 *
 */
private static final long serialVersionUID = 4549798608972299810L;

private int statusCode;

public NetworkException(final String message) {
	super(message);
}

public NetworkException(final Throwable cause) {
	super(cause);
}

public NetworkException(final String message, final Throwable cause) {
	super(message, cause);
}

public NetworkException(final Throwable cause, final int statusCode) {
	super("Status-Code: " + statusCode, cause);
	this.statusCode = statusCode;
}

public int getStatusCode() {
	return statusCode;
}
}
