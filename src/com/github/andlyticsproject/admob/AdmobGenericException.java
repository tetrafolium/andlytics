
package com.github.andlyticsproject.admob;

public class AdmobGenericException extends AdmobException {

private static final long serialVersionUID = 1L;

public AdmobGenericException(final String string) {
	super(string);
}

public AdmobGenericException(final Exception e) {
	super(e);
}

public AdmobGenericException(final Exception e, final String sb) {
	super(sb, e);
}
}
