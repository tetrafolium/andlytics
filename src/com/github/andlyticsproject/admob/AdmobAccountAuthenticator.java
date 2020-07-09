
package com.github.andlyticsproject.admob;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.github.andlyticsproject.AdmobAuthenticatorActivity;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator
 * in onBind()
 */
public class AdmobAccountAuthenticator extends Service {

    public static final String AUTHTOKEN_TYPE_ADMOB = "com.github.andlyticsproject.admob";
    public static final String ACCOUNT_TYPE_ADMOB = "com.github.andlyticsproject.admob";

    private static Authenticator sAccountAuthenticator = null;

    public AdmobAccountAuthenticator() {
        super();
    }

    public IBinder onBind(final Intent intent) {
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            ret = getAuthenticator().getIBinder();
        return ret;
    }

    private Authenticator getAuthenticator() {
        if (sAccountAuthenticator == null)
            sAccountAuthenticator = new Authenticator(this);
        return sAccountAuthenticator;
    }

    private static class Authenticator extends AbstractAccountAuthenticator {

        // Authentication Service context
        private final Context mContext;

        public Authenticator(final Context context) {
            super(context);
            mContext = context;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType,
                                 final String authTokenType, final String[] requiredFeatures, final Bundle options) {
            final Intent intent = new Intent(mContext, AdmobAuthenticatorActivity.class);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account,
                                         final Bundle options) {
            if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
                final String password = options.getString(AccountManager.KEY_PASSWORD);
                final String token = onlineConfirmPassword(account.name, password);
                final Bundle result = new Bundle();
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, token == null ? false : true);
                return result;
            }
            // Launch AuthenticatorActivity to confirm credentials
            final Intent intent = new Intent(mContext, AdmobAuthenticatorActivity.class);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_USERNAME, account.name);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_CONFIRM_CREDENTIALS, true);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account,
                                   final String authTokenType, final Bundle loginOptions) {
            if (!authTokenType.equals(AdmobAccountAuthenticator.AUTHTOKEN_TYPE_ADMOB)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
                return result;
            }
            final AccountManager am = AccountManager.get(mContext);
            final String password = am.getPassword(account);
            if (password != null) {
                final String token = onlineConfirmPassword(account.name, password);
                if (token != null) {
                    final Bundle result = new Bundle();
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, AdmobAccountAuthenticator.ACCOUNT_TYPE_ADMOB);
                    result.putString(AccountManager.KEY_AUTHTOKEN, token);
                    return result;
                }
            }
            // the password was missing or incorrect, return an Intent to an
            // Activity that will prompt the user for the password.
            final Intent intent = new Intent(mContext, AdmobAuthenticatorActivity.class);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_USERNAME, account.name);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getAuthTokenLabel(final String authTokenType) {
            if (authTokenType.equals(AdmobAccountAuthenticator.AUTHTOKEN_TYPE_ADMOB)) {
                return mContext.getString(com.github.andlyticsproject.R.string.app_name_admob);
            }
            return null;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account,
                                  final String[] features) {
            final Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
            return result;
        }

        /**
         * Validates user's password on the server
         */
        private String onlineConfirmPassword(final String username, final String password) {
            return AdmobAuthenticationUtilities.authenticate(username, password,
                    null/* Handler */, null/* Context */);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Bundle updateCredentials(final AccountAuthenticatorResponse response, final Account account,
                                        final String authTokenType, final Bundle loginOptions) {
            final Intent intent = new Intent(mContext, AdmobAuthenticatorActivity.class);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_USERNAME, account.name);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
            intent.putExtra(AdmobAuthenticatorActivity.PARAM_CONFIRM_CREDENTIALS, false);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }
    }
}
