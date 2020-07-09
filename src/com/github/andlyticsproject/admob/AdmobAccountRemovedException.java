
package com.github.andlyticsproject.admob;

public class AdmobAccountRemovedException extends AdmobException {

private static final long serialVersionUID = 1L;

private String accountName;

public AdmobAccountRemovedException(final String string,
                                    final String accountName) {
	super(string);
	this.setAccountName(accountName);
}

public void setAccountName(final String accountName) {
	this.accountName = accountName;
}

public String getAccountName() {
	return accountName;
}
}
