
package com.github.andlyticsproject.io;

public class ServiceException extends Exception {

private static final long serialVersionUID = 1L;

public ServiceException(final Exception e) {
	super(e);
}
}
