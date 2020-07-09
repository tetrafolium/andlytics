package com.github.andlyticsproject.console;

public class AuthenticationException extends DevConsoleException {

    /**
     *
     */
    private static final long serialVersionUID = -5409862768941335087L;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(final String message) {
        super(message);
    }

    public AuthenticationException(final Throwable cause) {
        super(cause);
    }

    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
