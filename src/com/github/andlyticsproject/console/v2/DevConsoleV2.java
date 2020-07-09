package com.github.andlyticsproject.console.v2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import com.github.andlyticsproject.console.AuthenticationException;
import com.github.andlyticsproject.console.DevConsole;
import com.github.andlyticsproject.console.DevConsoleException;
import com.github.andlyticsproject.console.NetworkException;
import com.github.andlyticsproject.model.AppInfo;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.Comment;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;
import com.github.andlyticsproject.model.RevenueSummary;
import com.github.andlyticsproject.util.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This is a WIP class representing the new v2 version of the developer console.
 * The aim is to build it from scratch to make it a light weight and as well
 * documented at the end as possible. Once it is done and available to all
 * users, we will rip out the old code and replace it with this.
 *
 * Once v2 is available to all users, there is scope for better utilising the
 * available statistics data, so keep that in mind when developing this class.
 * For now though, keep it simple and get it working.
 *
 * See https://github.com/AndlyticsProject/andlytics/wiki/Developer-Console-v2
 * for some more documentation
 *
 * This class fetches the data, which is then passed using {@link JsonParser}
 *
 */
@SuppressLint("DefaultLocale")
public class DevConsoleV2 implements DevConsole {

  // 30 seconds -- for both socket and connection
  public static final int TIMEOUT = 30 * 1000;

  private static final String TAG = DevConsoleV2.class.getSimpleName();

  private static final boolean DEBUG = false;

  private DefaultHttpClient httpClient;
  private DevConsoleAuthenticator authenticator;
  private String accountName;
  private DevConsoleV2Protocol protocol;

  private ResponseHandler<String> responseHandler =
      HttpClientFactory.createResponseHandler();

  public static DevConsoleV2
  createForAccount(final String accountName,
                   final DefaultHttpClient httpClient) {
    DevConsoleAuthenticator authenticator =
        new OauthAccountManagerAuthenticator(accountName, httpClient);

    return new DevConsoleV2(httpClient, authenticator,
                            new DevConsoleV2Protocol());
  }

  public static DevConsoleV2
  createForAccountAndPassword(final String accountName, final String password,
                              final DefaultHttpClient httpClient) {
    DevConsoleAuthenticator authenticator =
        new PasswordAuthenticator(accountName, password, httpClient);

    return new DevConsoleV2(httpClient, authenticator,
                            new DevConsoleV2Protocol());
  }

  private DevConsoleV2(final DefaultHttpClient httpClient,
                       final DevConsoleAuthenticator authenticator,
                       final DevConsoleV2Protocol protocol) {
    this.httpClient = httpClient;
    this.authenticator = authenticator;
    this.accountName = authenticator.getAccountName();
    this.protocol = protocol;
  }

  /**
   * Gets a list of available apps for the given account
   *
   * @param activity
   * @return
   * @throws DevConsoleException
   */
  public synchronized List<AppInfo> getAppInfo(final Activity activity)
      throws DevConsoleException {
    try {
      // the authenticator launched a sub-activity, bail out for now
      if (!authenticateWithCachedCredentialas(activity)) {
        return new ArrayList<AppInfo>();
      }

      return fetchAppInfosAndStatistics();
    } catch (AuthenticationException ex) {
      if (!authenticateFromScratch(activity)) {
        return new ArrayList<AppInfo>();
      }

      return fetchAppInfosAndStatistics();
    }
  }

  private List<AppInfo> fetchAppInfosAndStatistics() {
    // Fetch a list of available apps
    List<AppInfo> apps = fetchAppInfos();

    for (AppInfo app : apps) {
      // Fetch remaining app statistics
      // Latest stats object, and active/total installs is fetched
      // in fetchAppInfos
      AppStats stats = app.getLatestStats();
      fetchRatings(app, stats);

      RevenueSummary revenue = fetchRevenueSummary(app);
      app.setTotalRevenueSummary(revenue);
      // this is currently the same as the last item of the historical
      // data, so save some cycles and don't parse historical
      // XXX the definition of 'last day' is unclear: GMT?
      if (revenue != null) {
        stats.setTotalRevenue(revenue.getLastDay());
      }
    }

    return apps;
  }

  /**
   * Gets a list of comments for the given app based on the startIndex and
   * count
   *
   * @param accountName
   * @param packageName
   * @param startIndex
   * @param count
   * @return
   * @throws DevConsoleException
   */
  public synchronized List<Comment>
  getComments(final Activity activity, final String packageName,
              final String developerId, final int startIndex, final int count,
              final String displayLocale) throws DevConsoleException {
    try {
      if (!authenticateWithCachedCredentialas(activity)) {
        return new ArrayList<Comment>();
      }

      return fetchComments(packageName, developerId, startIndex, count,
                           displayLocale);
    } catch (AuthenticationException ex) {
      if (!authenticateFromScratch(activity)) {
        return new ArrayList<Comment>();
      }

      return fetchComments(packageName, developerId, startIndex, count,
                           displayLocale);
    }
  }

  public synchronized Comment replyToComment(final Activity activity,
                                             final String packageName,
                                             final String developerId,
                                             final String commentUniqueId,
                                             final String reply) {
    try {
      if (!authenticateWithCachedCredentialas(activity)) {
        return null;
      }

      return replyToComment(packageName, developerId, commentUniqueId, reply);
    } catch (AuthenticationException ex) {
      if (!authenticateFromScratch(activity)) {
        return null;
      }

      return replyToComment(packageName, developerId, commentUniqueId, reply);
    }
  }

  private Comment replyToComment(final String packageName,
                                 final String developerId,
                                 final String commentUiqueId,
                                 final String reply) {
    String response = post(protocol.createCommentsUrl(developerId),
                           protocol.createReplyToCommentRequest(
                               packageName, commentUiqueId, reply),
                           developerId);

    return protocol.parseCommentReplyResponse(response);
  }

  /**
   * Fetches a combined list of apps for all available console accounts
   *
   * @return combined list of apps
   * @throws DevConsoleException
   */
  private List<AppInfo> fetchAppInfos() throws DevConsoleException {
    List<AppInfo> result = new ArrayList<AppInfo>();
    for (DeveloperConsoleAccount consoleAccount :
         protocol.getSessionCredentials().getDeveloperConsoleAccounts()) {
      String developerId = consoleAccount.getDeveloperId();
      if (!consoleAccount.getCanAccessApps()) {
        Log.w(TAG, "Not allowed to fetch app info for " + developerId);
        continue;
      }
      Log.d(TAG, "Getting apps for " + developerId);
      String response =
          post(protocol.createFetchAppsUrl(developerId),
               protocol.createFetchAppInfosRequest(), developerId);

      // don't skip incomplete apps, so we can get the package list
      List<AppInfo> apps =
          protocol.parseAppInfosResponse(response, accountName, false);
      if (apps.isEmpty()) {
        continue;
      }

      for (AppInfo appInfo : apps) {
        appInfo.setDeveloperId(developerId);
        appInfo.setDeveloperName(consoleAccount.getName());
      }

      result.addAll(apps);
      List<String> incompletePackages = new ArrayList<String>();
      for (AppInfo app : apps) {
        if (app.isIncomplete()) {
          result.remove(app);
          incompletePackages.add(app.getPackageName());
        }
      }
      Log.d(TAG,
            String.format("Found %d apps for %s", apps.size(), developerId));
      Log.d(TAG, String.format("Incomplete packages: %d",
                               incompletePackages.size()));

      if (incompletePackages.isEmpty()) {
        continue;
      }

      Log.d(TAG,
            String.format("Got %d incomplete apps, issuing details request",
                          incompletePackages.size()));
      response = post(protocol.createFetchAppsUrl(developerId),
                      protocol.createFetchAppInfosRequest(incompletePackages),
                      developerId);
      // if info is not here, not much to do, skip
      List<AppInfo> extraApps =
          protocol.parseAppInfosResponse(response, accountName, true);
      Log.d(TAG, String.format("Got %d extra apps from details request",
                               extraApps.size()));
      for (AppInfo appInfo : extraApps) {
        appInfo.setDeveloperId(developerId);
        appInfo.setDeveloperName(consoleAccount.getName());
      }
      result.addAll(extraApps);
    }

    return result;
  }

  /**
   * Fetches statistics for the given packageName of the given statsType and
   * adds them to the given {@link AppStats} object
   *
   * This is not used as statistics can be fetched via fetchAppInfos Can use
   * it later to get historical etc data
   *
   * @param packageName
   * @param stats
   * @param statsType
   * @throws DevConsoleException
   */
  @SuppressWarnings("unused")
  private void fetchStatistics(final AppInfo appInfo, final AppStats stats,
                               final int statsType) throws DevConsoleException {
    String developerId = appInfo.getDeveloperId();
    String response = post(protocol.createFetchStatisticsUrl(developerId),
                           protocol.createFetchStatisticsRequest(
                               appInfo.getPackageName(), statsType),
                           developerId);
    protocol.parseStatisticsResponse(response, stats, statsType);
  }

  /**
   * Fetches ratings for the given packageName and adds them to the given {@link
   * AppStats} object
   *
   * @param packageName
   * The app to fetch ratings for
   * @param stats
   * The AppStats object to add them to
   * @throws DevConsoleException
   */
  private void fetchRatings(final AppInfo appInfo, final AppStats stats)
      throws DevConsoleException {
    String developerId = appInfo.getDeveloperId();
    String response =
        post(protocol.createCommentsUrl(developerId),
             protocol.createFetchRatingsRequest(appInfo.getPackageName()),
             developerId);
    protocol.parseRatingsResponse(response, stats);
  }

  private List<Comment> fetchComments(final String packageName,
                                      final String developerId,
                                      final int startIndex, final int count,
                                      final String displayLocale)
      throws DevConsoleException {
    List<Comment> comments = new ArrayList<Comment>();
    String response = post(protocol.createCommentsUrl(developerId),
                           protocol.createFetchCommentsRequest(
                               packageName, startIndex, count, displayLocale),
                           developerId);
    comments.addAll(protocol.parseCommentsResponse(response));

    return comments;
  }

  private RevenueSummary fetchRevenueSummary(final AppInfo appInfo)
      throws DevConsoleException {
    try {
      String developerId = appInfo.getDeveloperId();
      String response = post(
          protocol.createRevenueUrl(developerId),
          protocol.createFetchRevenueSummaryRequest(appInfo.getPackageName()),
          developerId);

      return protocol.parseRevenueResponse(response);
    } catch (NetworkException e) {
      // XXX not pretty, maybe use a dedicated exception?
      // if we don't have 'view financial info' permission for an app
      // getting revenue returns 403.
      // same sems to apply for 500
      if (e.getStatusCode() == HttpStatus.SC_FORBIDDEN ||
          e.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
        return null;
      }

      throw e;
    }
  }

  private boolean authenticateWithCachedCredentialas(final Activity activity) {
    return authenticate(activity, false);
  }

  private boolean authenticateFromScratch(final Activity activity) {
    return authenticate(activity, true);
  }

  /**
   * Logs into the Android Developer Console
   *
   * @param reuseAuthentication
   * @throws DevConsoleException
   */
  private boolean authenticate(final Activity activity,
                               final boolean invalidateCredentials)
      throws DevConsoleException {
    if (invalidateCredentials) {
      protocol.invalidateSessionCredentials();
    }

    if (protocol.hasSessionCredentials()) {
      // nothing to do
      return true;
    }

    SessionCredentials sessionCredentials =
        activity == null
            ? authenticator.authenticateSilently(invalidateCredentials)
            : authenticator.authenticate(activity, invalidateCredentials);
    protocol.setSessionCredentials(sessionCredentials);

    return protocol.hasSessionCredentials();
  }

  private String post(final String url, final String postData,
                      final String developerId) {
    try {
      HttpPost post = new HttpPost(url);
      protocol.addHeaders(post, developerId);
      post.setEntity(new StringEntity(postData, "UTF-8"));

      if (DEBUG) {
        CookieStore cookieStore = httpClient.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie c : cookies) {
          Log.d(TAG, String.format("****Cookie**** %s=%s", c.getName(),
                                   c.getValue()));
        }
      }

      return httpClient.execute(post, responseHandler);
    } catch (HttpResponseException e) {
      if (e.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
        throw new AuthenticationException(e);
      }

      throw new NetworkException(e, e.getStatusCode());
    } catch (IOException e) {
      throw new NetworkException(e);
    }
  }

  public boolean canReplyToComments() { return protocol.canReplyToComments(); }

  public boolean hasSessionCredentials() {
    return protocol.hasSessionCredentials();
  }
}
