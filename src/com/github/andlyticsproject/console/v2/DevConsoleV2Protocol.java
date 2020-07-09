package com.github.andlyticsproject.console.v2;

import android.annotation.SuppressLint;

import com.github.andlyticsproject.console.DevConsoleProtocolException;
import com.github.andlyticsproject.model.AppInfo;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.Comment;
import com.github.andlyticsproject.model.RevenueSummary;
import com.github.andlyticsproject.util.FileUtils;
import com.google.gson.JsonObject;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@SuppressLint("DefaultLocale")
public class DevConsoleV2Protocol {

    // Base urls
    static final String URL_DEVELOPER_CONSOLE = "https://play.google.com/apps/publish";
    static final String URL_APPS = DevConsoleV2Protocol.URL_DEVELOPER_CONSOLE + "/androidapps";
    static final String URL_STATISTICS = DevConsoleV2Protocol.URL_DEVELOPER_CONSOLE + "/statistics";
    static final String URL_REVIEWS = DevConsoleV2Protocol.URL_DEVELOPER_CONSOLE + "/reviews";

    // Templates for payloads used in POST requests
    static final String FETCH_APPS_TEMPLATE = "{\"method\":\"fetch\","
            + "\"params\":{\"2\":1,\"3\":7},\"xsrf\":\"%s\"}";
    // 1$: comma separated list of package names
    static final String FETCH_APPS_BY_PACKAGES_TEMPLATE = "{\"method\":\"fetch\","
            + "\"params\":{\"1\":[%1$s],\"3\":1},\"xsrf\":\"%2$s\"}";
    // 1$: package name, 2$: XSRF
    static final String FETCH_APP_TEMPLATE = "{\"method\":\"fetch\","
            + "\"params\":{\"1\":[\"%1$s\"],\"3\":0},\"xsrf\":\"%2$s\"}";
    // 1$: package name, 2$: XSRF
    static final String GET_RATINGS_TEMPLATE = "{\"method\":\"getRatings\","
            + "\"params\":{\"1\":[\"%1$s\"]},\"xsrf\":\"%2$s\"}";
    // 1$: package name, 2$: start, 3$: num comments to fetch, 4$: display locale, 5$ XSRF
    static final String GET_REVIEWS_TEMPLATE = "{\"method\":\"getReviews\","
            + "\"params\":{\"1\":\"%1$s\",\"2\":%2$d,\"3\":%3$d,\"8\":\"%4$s\",\"10\":0,\"18\":1},\"xsrf\":\"%5$s\"}";
    // 1$: package name, 2$: stats type, 3$: stats by, 4$: XSRF
    static final String GET_COMBINED_STATS_TEMPLATE = "{\"method\":\"getCombinedStats\","
            + "\"params\":{\"1\":\"%1$s\",\"2\":1,\"3\":%2$d,\"4\":[%3$d]},\"xsrf\":\"%4$s\"}";
    // %1$s: package name, %2$s: XSRF
    static final String REVENUE_SUMMARY_TEMPLATE = "{\"method\":\"revenueSummary\",\"params\":{\"1\":\"%1$s\",\"2\":\"\"},\"xsrf\":\"%2$s\"}";
    // %1$s: package name, %2$s: XSRF
    static final String REVENUE_HISTORICAL_DATA = "{\"method\":\"historicalData\",\"params\":{\"1\":\"%1$s\",\"2\":\"\"},\"xsrf\":\"%2$s\"}";

    static final String REPLY_TO_COMMENTS_FEATURE = "REPLY_TO_COMMENTS";

    // Represents the different ways to break down statistics by e.g. by android
    // version
    static final int STATS_BY_ANDROID_VERSION = 1;
    static final int STATS_BY_DEVICE = 2;
    static final int STATS_BY_COUNTRY = 3;
    static final int STATS_BY_LANGUAGE = 4;
    static final int STATS_BY_APP_VERSION = 5;
    static final int STATS_BY_CARRIER = 6;

    // Represents the different types of statistics e.g. active device installs
    static final int STATS_TYPE_ACTIVE_DEVICE_INSTALLS = 1;
    static final int STATS_TYPE_TOTAL_USER_INSTALLS = 8;

    static final int COMMENT_REPLY_MAX_LENGTH = 350;

    private SessionCredentials sessionCredentials;

    DevConsoleV2Protocol() {
    }

    DevConsoleV2Protocol(final SessionCredentials sessionCredentials) {
        this.sessionCredentials = sessionCredentials;
    }

    SessionCredentials getSessionCredentials() {
        return sessionCredentials;
    }

    void setSessionCredentials(final SessionCredentials sessionCredentials) {
        this.sessionCredentials = sessionCredentials;
    }

    boolean hasSessionCredentials() {
        return sessionCredentials != null;
    }

    void invalidateSessionCredentials() {
        sessionCredentials = null;
    }

    private void checkState() {
        if (sessionCredentials == null) {
            throw new IllegalStateException("Set session credentials first.");
        }
    }

    void addHeaders(final HttpPost post, final String developerId) {
        checkState();

        post.addHeader("Host", "play.google.com");
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Content-Type", "application/javascript; charset=UTF-8");
        // XXX get this dynamically by fetching and executing the nocache.js file:
        // https://play.google.com/apps/publish/v2/gwt/com.google.wireless.android.vending.developer.fox.Fox.nocache.js
        post.addHeader("X-GWT-Permutation", "7E419416D8BA779A68D417481802D188");
        post.addHeader("Origin", "https://play.google.com");
        post.addHeader("X-GWT-Module-Base", "https://play.google.com/apps/publish/gwt/");
        post.addHeader("Referer", "https://play.google.com/apps/publish/?dev_acc=" + developerId);
    }

    String createDeveloperUrl(final String baseUrl, final String developerId) {
        checkState();

        return String.format("%s?dev_acc=%s", baseUrl, developerId);
    }

    String createFetchAppsUrl(final String developerId) {
        return createDeveloperUrl(URL_APPS, developerId);
    }

    String createFetchStatisticsUrl(final String developerId) {
        return createDeveloperUrl(URL_STATISTICS, developerId);
    }

    String createCommentsUrl(final String developerId) {
        return createDeveloperUrl(URL_REVIEWS, developerId);
    }

    String createRevenueUrl(final String developerId) {
        return createDeveloperUrl(URL_STATISTICS, developerId);
    }

    String createFetchAppInfosRequest() {
        checkState();

        // TODO Check the remaining possible parameters to see if they are
        // needed for large numbers of apps
        return String.format(FETCH_APPS_TEMPLATE, sessionCredentials.getXsrfToken());
    }

    String createFetchAppInfosRequest(final List<String> packages) {
        checkState();

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < packages.size(); i++) {
            String packageName = packages.get(i);
            buff.append(packageName);
            if (i != packages.size() - 1) {
                buff.append(",");
            }
        }
        String packageList = buff.toString();

        return String.format(FETCH_APPS_BY_PACKAGES_TEMPLATE, packageList,
                             sessionCredentials.getXsrfToken());
    }

    List<AppInfo> parseAppInfosResponse(final String json, final String accountName, final boolean skipIncomplete) {
        try {
            return JsonParser.parseAppInfos(json, accountName, skipIncomplete);
        } catch (JSONException ex) {
            saveDebugJson(json);
            throw new DevConsoleProtocolException(json, ex);
        }
    }

    private static void saveDebugJson(final String json) {
        FileUtils.tryWriteToDebugDir(
            String.format("console_reply_%d.json", System.currentTimeMillis()), json);
    }

    String createFetchAppInfoRequest(final String packageName) {
        checkState();

        return String.format(FETCH_APP_TEMPLATE, packageName, sessionCredentials.getXsrfToken());
    }

    String createFetchStatisticsRequest(final String packageName, final int statsType) {
        checkState();

        // Don't care about the breakdown at the moment:
        // STATS_BY_ANDROID_VERSION
        return String.format(GET_COMBINED_STATS_TEMPLATE, packageName, statsType,
                             STATS_BY_ANDROID_VERSION, sessionCredentials.getXsrfToken());
    }

    void parseStatisticsResponse(final String json, final AppStats stats, final int statsType) {
        try {
            JsonParser.parseStatistics(json, stats, statsType);
        } catch (JSONException ex) {
            saveDebugJson(json);
            throw new DevConsoleProtocolException(json, ex);
        }
    }

    String createFetchRatingsRequest(final String packageName) {
        checkState();

        return String.format(GET_RATINGS_TEMPLATE, packageName, sessionCredentials.getXsrfToken());
    }

    void parseRatingsResponse(final String json, final AppStats stats) {
        try {
            JsonParser.parseRatings(json, stats);
        } catch (JSONException ex) {
            saveDebugJson(json);
            throw new DevConsoleProtocolException(json, ex);
        }
    }

    String createFetchCommentsRequest(final String packageName, final int start, final int pageSize,
                                      final String displayLocale) {
        checkState();

        return String.format(GET_REVIEWS_TEMPLATE, packageName, start, pageSize, displayLocale,
                             sessionCredentials.getXsrfToken());
    }

    String createReplyToCommentRequest(final String packageName, final String commentId, final String reply) {
        checkState();

        if (!canReplyToComments()) {
            throw new IllegalStateException(
                "Reply to comments feature not available for this account");
        }

        // XXX we can probably do better, truncate for now
        if (reply.length() > COMMENT_REPLY_MAX_LENGTH) {
            reply = reply.substring(0, COMMENT_REPLY_MAX_LENGTH);
        }

        JsonObject replyObj = new JsonObject();
        replyObj.addProperty("method", "sendReply");
        JsonObject params = new JsonObject();
        params.addProperty("1", packageName);
        params.addProperty("2", commentId);
        params.addProperty("3", reply);
        replyObj.add("params", params);
        replyObj.addProperty("xsrf", sessionCredentials.getXsrfToken());

        return replyObj.toString();
    }

    boolean hasFeature(final String feature) {
        checkState();

        return sessionCredentials.hasFeature(feature);
    }

    boolean canReplyToComments() {
        // this has apparently been removed because now everybody can
        // reply to comments
        //              return hasFeature(REPLY_TO_COMMENTS_FEATURE);
        return true;
    }

    List<Comment> parseCommentsResponse(final String json) {
        try {
            return JsonParser.parseComments(json);
        } catch (JSONException ex) {
            saveDebugJson(json);
            throw new DevConsoleProtocolException(json, ex);
        }
    }

    Comment parseCommentReplyResponse(final String json) {
        try {
            return JsonParser.parseCommentReplyResponse(json);
        } catch (JSONException ex) {
            saveDebugJson(json);
            throw new DevConsoleProtocolException(json, ex);
        }
    }

    String createFetchRevenueSummaryRequest(final String packageName) {
        checkState();

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("method", "fetchStats");
            jsonObj.put("xsrf", sessionCredentials.getXsrfToken());

            JSONObject paramsObj = new JSONObject();
            jsonObj.put("params", paramsObj);

            JSONArray paramOne = new JSONArray();
            paramsObj.put("1", paramOne);

            JSONObject firstElem = new JSONObject();
            firstElem.put("1", new JSONObject().put("1", packageName).put("2", "1"));
            firstElem.put("2", -1);
            firstElem.put("3", -1);

            JSONArray arr = new JSONArray();
            arr.put(new JSONObject().put("1", 11).put("2",
                    new JSONArray().put(sessionCredentials.getPreferredCurrency())));
            arr.put(new JSONObject().put("1", 18).put("2",
                    // summary, last day, last 7, last 30
                    new JSONArray().put("-1").put("1").put("7").put("30")));
            firstElem.put("6", arr);

            firstElem.put("7", new JSONArray().put(18));
            firstElem.put("8", new JSONArray().put(17));

            paramOne.put(firstElem);

            return jsonObj.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    RevenueSummary parseRevenueResponse(final String json) {
        try {
            return JsonParser.parseRevenueResponse(json, sessionCredentials.getPreferredCurrency());
        } catch (JSONException ex) {
            saveDebugJson(json);
            throw new DevConsoleProtocolException(json, ex);
        }
    }

    String createFetchHistoricalRevenueRequest(final String packageName) {
        checkState();

        return String.format(REVENUE_HISTORICAL_DATA, packageName,
                             sessionCredentials.getXsrfToken());
    }

}
