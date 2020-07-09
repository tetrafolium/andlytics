package com.github.andlyticsproject.console.v2;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.github.andlyticsproject.AndlyticsApp;
import com.github.andlyticsproject.R;
import com.github.andlyticsproject.console.AuthenticationException;
import com.github.andlyticsproject.console.NetworkException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AccountManagerAuthenticator extends BaseAuthenticator {

    private static final String TAG = AccountManagerAuthenticator.class.getSimpleName();

    private static final String DEVELOPER_CONSOLE_URL = "https://play.google.com/apps/publish/";
    private static final Uri ISSUE_AUTH_TOKEN_URL = Uri
            .parse("https://www.google.com/accounts/IssueAuthToken?service=gaia&Session=false");
    private static final Uri TOKEN_AUTH_URL = Uri
            .parse("https://www.google.com/accounts/TokenAuth");

    private static final int REQUEST_AUTHENTICATE = 42;

    private static final boolean DEBUG = false;

    private AccountManager accountManager;

    // includes one-time token
    private String webloginUrl;

    private DefaultHttpClient httpClient;

    public AccountManagerAuthenticator(String accountName, DefaultHttpClient httpClient) {
        super(accountName);
        this.accountManager = AccountManager.get(AndlyticsApp.getInstance());
        this.httpClient = httpClient;
    }

    // as described here: http://www.slideshare.net/pickerweng/chromium-os-login
    // http://www.chromium.org/chromium-os/chromiumos-design-docs/login
    // and implemented by the Android Browser:
    // packages/apps/Browser/src/com/android/browser/GoogleAccountLogin.java
    // packages/apps/Browser/src/com/android/browser/DeviceAccountLogin.java
    @Override
    public SessionCredentials authenticate(Activity activity, boolean invalidate)
    throws AuthenticationException {
        return authenticateInternal(activity, invalidate);
    }

    @Override
    public SessionCredentials authenticateSilently(boolean invalidate)
    throws AuthenticationException {
        return authenticateInternal(null, invalidate);
    }

    @SuppressWarnings("deprecation")
    private SessionCredentials authenticateInternal(Activity activity, boolean invalidate)
    throws AuthenticationException {
        try {
            Account[] accounts = accountManager.getAccountsByType("com.google");
            Account account = null;
            for (Account acc : accounts) {
                if (acc.name.equals(accountName)) {
                    account = acc;
                    break;
                }
            }
            if (account == null) {
                throw new AuthenticationException(String.format("Account %s not found on device?",
                                                  accountName));
            }

            if (invalidate && webloginUrl != null) {
                // probably not needed, since what we are getting is a very
                // short-lived token
                accountManager.invalidateAuthToken(account.type, webloginUrl);
            }

            Bundle authResult = accountManager.getAuthToken(account,
                                "weblogin:service=androiddeveloper&continue=" + DEVELOPER_CONSOLE_URL, false,
                                null, null).getResult();
            if (authResult.containsKey(AccountManager.KEY_INTENT)) {
                Intent authIntent = authResult.getParcelable(AccountManager.KEY_INTENT);
                if (DEBUG) {
                    Log.w(TAG, "Got a reauthenticate intent: " + authIntent);
                }

                // silent mode, show notification
                if (activity == null) {
                    Context ctx = AndlyticsApp.getInstance();
                    Builder builder = new NotificationCompat.Builder(ctx);
                    builder.setSmallIcon(R.drawable.statusbar_andlytics);
                    builder.setContentTitle(ctx.getResources().getString(R.string.auth_error,
                                            accountName));
                    builder.setContentText(ctx.getResources().getString(R.string.auth_error,
                                           accountName));
                    builder.setAutoCancel(true);
                    PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                                                  accountName.hashCode(), authIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);

                    NotificationManager nm = (NotificationManager) ctx
                                             .getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(accountName.hashCode(), builder.build());

                    return null;
                }

                // activity mode, start activity
                authIntent.setFlags(authIntent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(authIntent, REQUEST_AUTHENTICATE);

                return null;
            }
            webloginUrl = authResult.getString(AccountManager.KEY_AUTHTOKEN);
            if (webloginUrl == null) {
                throw new AuthenticationException(
                    "Unexpected authentication error: weblogin URL = null");
            }
            if (DEBUG) {
                Log.d(TAG, "Weblogin URL: " + webloginUrl);
            }

            if (!webloginUrl.contains("MergeSession") || webloginUrl.contains("WILL_NOT_SIGN_IN")) {
                Log.d(TAG, "Most probably additional verification is required, "
                      + "opening browser");

                String sid = accountManager
                             .getAuthToken(account, "SID", null, activity, null, null).getResult()
                             .getString(AccountManager.KEY_AUTHTOKEN);
                if (sid == null) {
                    throw new AuthenticationException("Authentication error: cannot get SID.");
                }
                String lsid = accountManager
                              .getAuthToken(account, "LSID", null, activity, null, null).getResult()
                              .getString(AccountManager.KEY_AUTHTOKEN);
                if (lsid == null) {
                    throw new AuthenticationException("Authentication error: cannot get LSID.");
                }

                String url = ISSUE_AUTH_TOKEN_URL.buildUpon().appendQueryParameter("SID", sid)
                             .appendQueryParameter("LSID", lsid).build().toString();
                HttpPost getUberToken = new HttpPost(url);
                HttpResponse response = httpClient.execute(getUberToken);
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_UNAUTHORIZED) {
                    throw new AuthenticationException("Cannot get uber token: "
                                                      + response.getStatusLine());
                }
                String uberToken = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (uberToken == null || "".equals(uberToken)) {
                    throw new AuthenticationException("Invalid ubertoken");
                }
                if (DEBUG) {
                    Log.d(TAG, "Uber token: " + uberToken);
                }

                webloginUrl = TOKEN_AUTH_URL.buildUpon()
                              .appendQueryParameter("source", "android-browser")
                              .appendQueryParameter("auth", uberToken)
                              .appendQueryParameter("continue", DEVELOPER_CONSOLE_URL).build().toString();
            }

            HttpGet getConsole = new HttpGet(webloginUrl);
            HttpResponse response = httpClient.execute(getConsole);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_UNAUTHORIZED) {
                throw new AuthenticationException("Authentication token expired: "
                                                  + response.getStatusLine());
            }
            if (status != HttpStatus.SC_OK) {
                throw new AuthenticationException("Authentication error: "
                                                  + response.getStatusLine());
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new AuthenticationException("Authentication error: null result?");
            }

            String responseStr = EntityUtils.toString(entity, "UTF-8");
            if (DEBUG) {
                Log.d(TAG, "Response: " + responseStr);
            }

            return createSessionCredentials(accountName, webloginUrl, responseStr,
                                            httpClient.getCookieStore().getCookies());
        } catch (IOException e) {
            throw new NetworkException(e);
        } catch (OperationCanceledException e) {
            throw new AuthenticationException(e);
        } catch (AuthenticatorException e) {
            throw new AuthenticationException(e);
        }
    }

}
