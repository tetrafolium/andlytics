package com.github.andlyticsproject.console;

public class DevConsoleException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 4251549102914653087L;

  public DevConsoleException() { super(); }

  public DevConsoleException(final String message) { super(message); }

  public DevConsoleException(final Throwable cause) { super(cause); }

  public DevConsoleException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
