package com.github.andlyticsproject.console;

public class DevConsoleProtocolException extends DevConsoleException {

/**
 *
 */
private static final long serialVersionUID = -6909987334134805822L;

private String postData;
private String consoleResponse;

public DevConsoleProtocolException(final String message) {
	super(message);
}

public DevConsoleProtocolException(final String message,
                                   final String postData,
                                   final String consoleResponse) {
	super(message);
	this.postData = postData;
	this.consoleResponse = consoleResponse;
}

public DevConsoleProtocolException(final Throwable cause) {
	super(cause);
}

public DevConsoleProtocolException(final String message,
                                   final Throwable cause) {
	super(message, cause);
}

public String getPostData() {
	return postData;
}

public String getConsoleResponse() {
	return consoleResponse;
}
}
