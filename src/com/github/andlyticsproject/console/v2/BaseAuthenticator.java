package com.github.andlyticsproject.console.v2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.github.andlyticsproject.AndlyticsApp;
import com.github.andlyticsproject.R;
import com.github.andlyticsproject.console.AppAccessBlockedException;
import com.github.andlyticsproject.console.AuthenticationException;
import com.github.andlyticsproject.console.DevConsoleException;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;
import com.github.andlyticsproject.util.FileUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseAuthenticator implements DevConsoleAuthenticator {

  private static final String TAG = BaseAuthenticator.class.getSimpleName();

  protected static final Pattern STARTUP_DATA_PATTERN =
      Pattern.compile("startupData = (\\{.+?\\});");

  protected String accountName;

  protected BaseAuthenticator(final String accountName) {
    this.accountName = accountName;
  }

  protected String findXsrfToken(final JSONObject startupData) {
    try {
      return new JSONObject(startupData.getString("XsrfToken")).getString("1");
    } catch (JSONException e) {
      throw new DevConsoleException(e);
    }
  }

  protected DeveloperConsoleAccount[] findDeveloperAccounts(
      final JSONObject startupData) {
    List<DeveloperConsoleAccount> devAccounts =
        new ArrayList<DeveloperConsoleAccount>();

    try {
      JSONObject devConsoleAccountsObj =
          new JSONObject(startupData.getString("DeveloperConsoleAccounts"));
      JSONArray devConsoleAccountsArr = devConsoleAccountsObj.getJSONArray("1");
      for (int i = 0; i < devConsoleAccountsArr.length(); i++) {
        JSONObject accountObj = devConsoleAccountsArr.getJSONObject(i);
        String developerId = accountObj.getString("1");
        String developerName =
            StringEscapeUtils.unescapeJava(accountObj.getString("2"));
        // Cannot access apps if e.g. a developer agreement needs to be accepted
        // XXX seems to be always false? Disable check for now
        //                              boolean canAccessApps =
        //                              accountObj.getBoolean("3");
        boolean canAccessApps = true;
        devAccounts.add(new DeveloperConsoleAccount(developerId, developerName,
                                                    canAccessApps));
      }

      return devAccounts.isEmpty()
          ? null
          : devAccounts.toArray(
                new DeveloperConsoleAccount[devAccounts.size()]);
    } catch (JSONException e) {
      throw new DevConsoleException(e);
    }
  }

  protected List<String> findWhitelistedFeatures(final JSONObject startupData) {
    List<String> result = new ArrayList<String>();

    try {
      JSONArray featuresArr =
          new JSONObject(startupData.getString("WhitelistedFeatures"))
              .getJSONArray("1");
      for (int i = 0; i < featuresArr.length(); i++) {
        result.add(featuresArr.getString(i));
      }

      return Collections.unmodifiableList(result);
    } catch (JSONException e) {
      throw new DevConsoleException(e);
    }
  }

  public JSONObject getStartupData(final String responseStr) {
    try {
      Matcher m = STARTUP_DATA_PATTERN.matcher(responseStr);
      if (m.find()) {
        String startupDataStr = m.group(1);
        return new JSONObject(startupDataStr);
      }

      return null;
    } catch (JSONException e) {
      throw new DevConsoleException(e);
    }
  }

  protected String findPreferredCurrency(final JSONObject startupData) {
    // fallback
    String result = "USD";

    try {
      JSONObject userDetails =
          new JSONObject(startupData.getString("UserDetails"));
      if (userDetails.has("2")) {
        result = userDetails.getString("2");
      }

      return result;
    } catch (JSONException e) {
      throw new DevConsoleException(e);
    }
  }

  public String getAccountName() { return accountName; }

  protected void debugAuthFailure(final String responseStr,
                                  final String webloginUrl) {
    FileUtils.writeToAndlyticsDir("console-response.html", responseStr);
    openAuthUrlInBrowser(webloginUrl);
  }

  protected void openAuthUrlInBrowser(final String webloginUrl) {
    if (webloginUrl == null) {
      Log.d(TAG, "Null webloginUrl?");
      return;
    }

    Log.d(TAG, "Opening login URL in browser: " + webloginUrl);

    Intent viewInBrowser = new Intent(Intent.ACTION_VIEW);
    viewInBrowser.setData(Uri.parse(webloginUrl));

    // Always show the notification
    // When this occurs, it can often occur in batches, e.g. if a the user also
    // clicks to view comments which results in multiple dev consoles opening in
    // their browser without an explanation. This is even worse if they have
    // multiple accounts and/or are currently signed in via a different account
    Context ctx = AndlyticsApp.getInstance();
    Builder builder = new NotificationCompat.Builder(ctx);
    builder.setSmallIcon(R.drawable.statusbar_andlytics);
    builder.setContentTitle(
        ctx.getResources().getString(R.string.auth_error, accountName));
    builder.setContentText(
        ctx.getResources().getString(R.string.auth_error_open_browser));
    builder.setAutoCancel(true);
    PendingIntent contentIntent =
        PendingIntent.getActivity(ctx, accountName.hashCode(), viewInBrowser,
                                  PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(contentIntent);

    NotificationManager nm =
        (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(accountName.hashCode(), builder.build());
  }

  protected SessionCredentials
  createSessionCredentials(final String accountName, final String webloginUrl,
                           final String responseStr,
                           final List<Cookie> cookies) {
    JSONObject startupData = getStartupData(responseStr);
    if (startupData == null) {
      debugAuthFailure(responseStr, webloginUrl);

      throw new AuthenticationException(
          "Couldn't find StartupData JSON object.");
    }

    DeveloperConsoleAccount[] developerAccounts =
        findDeveloperAccounts(startupData);
    if (developerAccounts == null) {
      debugAuthFailure(responseStr, webloginUrl);

      throw new AuthenticationException("Couldn't get developer account ID.");
    }
    boolean allowedToAccessAppsForSomeAccounts = false;
    for (DeveloperConsoleAccount account : developerAccounts) {
      if (account.getCanAccessApps()) {
        allowedToAccessAppsForSomeAccounts = true;
      } else {
        // TODO Report this to the user properly, but don't spam them because
        // they may never be able to resolve the problem e.g. the account owner
        // needs to agree to new terms
        Log.w(
            TAG,
            "Not allowed to fetch app info for " + account.getDeveloperId() +
                ". "
                +
                "Log into the account via your browser to resolve the problem.");
      }
    }
    if (!allowedToAccessAppsForSomeAccounts) {
      throw new AppAccessBlockedException(
          "Not allowed to fetch app info for any account.");
    }

    String xsrfToken = findXsrfToken(startupData);
    if (xsrfToken == null) {
      debugAuthFailure(responseStr, webloginUrl);

      throw new AuthenticationException("Couldn't get XSRF token.");
    }

    List<String> whitelistedFeatures = findWhitelistedFeatures(startupData);

    String preferredCurrency = findPreferredCurrency(startupData);

    SessionCredentials result =
        new SessionCredentials(accountName, xsrfToken, developerAccounts);
    result.addCookies(cookies);
    result.addWhitelistedFeatures(whitelistedFeatures);
    result.setPreferredCurrency(preferredCurrency);

    return result;
  }
}
