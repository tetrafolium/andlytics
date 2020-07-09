package com.github.andlyticsproject.console;

public class MultiAccountException extends DevConsoleException {

  /**
   *
   */
  private static final long serialVersionUID = -3841472223599618789L;

  public MultiAccountException(final String message) { super(message); }

  public MultiAccountException(final Throwable cause) { super(cause); }

  public MultiAccountException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
