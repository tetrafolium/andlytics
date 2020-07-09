
package com.github.andlyticsproject.admob;

// TODO Could make this a RE. Also need to review the diffrent admob exceptions
public class AdmobException extends Exception {

private static final long serialVersionUID = 1L;

public AdmobException(final String string) {
	super(string);
}

public AdmobException(final Exception e) {
	super(e);
}

public AdmobException(final String string, final Exception e) {
	super(string, e);
}
}
