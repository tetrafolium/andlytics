package com.github.andlyticsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.github.andlyticsproject.sync.AutosyncHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Preferences {

    // TODO Review this class an clean it up a bit

    public static final String PREF = "andlytics_pref";

    private static final String HIDDEN_ACCOUNT = "hiddenAccount";
    private static final String ACCOUNT_NAME = "accountName";
    private static final String GWTPERMUTATION = "permutation";

    private static final String POST_REQUEST_GET_FULL_ASSET_INFOS = "post_full_asset_info";
    private static final String POST_REQUEST_GET_USER_INFO_SIZE = "post_user_info_size";
    private static final String POST_REQUEST_USER_COMMENTS = "post_user_comments";
    private static final String POST_REQUEST_FEEDBACK = "post_feedback";

    public static final String AUTOSYNC_PERIOD = "autosync.period";
    public static final String AUTOSYNC_PERIOD_LAST_NON_ZERO = "autosync.period.last";
    private static final String CRASH_REPORT_DISABLE = "acra.enable";

    public static final String CHART_TIMEFRAME = "chart.timeframe";
    public static final String ADMOB_TIMEFRAME = "admob.timeframe";

    private static final String CHART_SMOOTH = "chart.smooth";
    private static final String SKIP_AUTO_LOGIN = "skip.auto.login";
    private static final String STATS_MODE = "stats.mode";

    public static final String NOTIFICATION_CHANGES_RATING = "notification.changes.rating";
    public static final String NOTIFICATION_CHANGES_COMMENTS = "notification.changes.comments";
    public static final String NOTIFICATION_CHANGES_DOWNLOADS = "notification.changes.download";
    public static final String NOTIFICATION_RINGTONE = "notification.ringtone";
    public static final String NOTIFICATION_LIGHT = "notification.light";
    public static final String NOTIFICATION_WHEN_ACCOUNT_VISISBLE = "notification.when_account_visible";

    public static final String DATE_FORMAT_LONG = "dateformat.long";

    private static final String ADMOB_HIDE_FOR_UNCONFIGURED_APPS = "admob.hide_for_unconfigured_apps";

    private static final String ADMOB_SITE_ID = "admob.siteid";

    private static final String ADMOB_ACCOUNT = "admob.account";

    private static final String SHOW_CHART_HINT = "show.chart.hint";

    private static final String LATEST_VERSION_CODE = "latest.version.code";

    private static final String LAST_STATS_REMOTE_UPDATE = "last.stats.remote.update";
    // 15 minutes in millis
    public static final long STATS_REMOTE_UPDATE_INTERVAL = 15 * 60 * 1000L;

    private static final String LAST_COMMENTS_REMOTE_UPDATE = "last.commentts.remote.update";
    // make it shorter for comments 5 minutes in millis
    public static final long COMMENTS_REMOTE_UPDATE_INTERVAL = 5 * 60 * 1000L;

    public static final String USE_GOOGLE_TRANSLATE_APP = "use.google.translate.app";
    public static final String SHOW_COMMENT_AUTO_TRANSLATIONS = "show.comment.auto.translations";

    public static final String SHOW_DEVELOPER_CUT_REVENUE = "revenue.show.developer.cut";

    public static final String NOTIFICATION_PREFS = "notification.prefs";

    public enum Timeframe {
        LAST_NINETY_DAYS, LAST_THIRTY_DAYS, UNLIMITED, LAST_TWO_DAYS, LATEST_VALUE, LAST_SEVEN_DAYS, MONTH_TO_DATE
    }

    public enum StatsMode {
        PERCENT, DAY_CHANGES
    }

    private static String cachedDateFormatShort;

    private static String cachedDateFormatLong;

    public static void disableCrashReports(final Context context) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.remove(CRASH_REPORT_DISABLE);
        editor.commit();
    }

    @Deprecated
    public static void saveAccountName(final Context activity, final String accountName) {
        SharedPreferences.Editor editor = getSettings(activity).edit();
        editor.putString(ACCOUNT_NAME, accountName);
        editor.commit();
    }

    @Deprecated
    public static void removeAccountName(final Context activity) {
        SharedPreferences.Editor editor = getSettings(activity).edit();
        editor.remove(ACCOUNT_NAME);
        editor.commit();
    }

    @Deprecated
    public static String getAccountName(final Context activity) {
        return getSettings(activity).getString(ACCOUNT_NAME, null);
    }

    private static SharedPreferences getSettings(final Context activity) {
        return activity.getSharedPreferences(PREF, 0);
    }

    public static String getGwtPermutation(final Context activity) {
        return getVersionDependingProperty(GWTPERMUTATION, activity);
    }

    public static void saveGwtPermutation(final Context activity, final String gwtPermutation) {
        saveVersionDependingProperty(GWTPERMUTATION, gwtPermutation, activity);
    }

    public static int getLastNonZeroAutosyncPeriod(final Context activity) {
        return getSettings(activity).getInt(AUTOSYNC_PERIOD_LAST_NON_ZERO,
                                            AutosyncHandler.DEFAULT_PERIOD);
    }

    public static void saveLastNonZeroAutosyncPeriod(final Context activity, final int syncPeriod) {
        SharedPreferences.Editor editor = getSettings(activity).edit();
        editor.putInt(AUTOSYNC_PERIOD_LAST_NON_ZERO, syncPeriod);
        editor.commit();
    }

    public static int getAutosyncPeriod(final Context activity) {
        // We use a ListPreference which only supports saving as strings, so
        // need to convert it when reading
        return Integer.parseInt(getSettings(activity).getString(AUTOSYNC_PERIOD,
                                Integer.toString(AutosyncHandler.DEFAULT_PERIOD)));
    }

    public static String getRequestFullAssetInfo(final Context activity) {
        return getVersionDependingProperty(POST_REQUEST_GET_FULL_ASSET_INFOS, activity);
    }

    public static void saveRequestFullAssetInfo(final Context activity, final String postdata) {
        saveVersionDependingProperty(POST_REQUEST_GET_FULL_ASSET_INFOS, postdata, activity);
    }

    public static String getRequestGetAssetForUserCount(final Context context) {
        return getVersionDependingProperty(POST_REQUEST_GET_USER_INFO_SIZE, context);
    }

    public static void saveRequestGetAssetForUserCount(final Context context, final String string) {
        saveVersionDependingProperty(POST_REQUEST_GET_USER_INFO_SIZE, string, context);
    }

    public static String getRequestUserComments(final Context context) {
        return getVersionDependingProperty(POST_REQUEST_USER_COMMENTS, context);
    }

    public static void saveRequestUserComments(final Context context, final String string) {
        saveVersionDependingProperty(POST_REQUEST_USER_COMMENTS, string, context);
    }

    private static String getVersionDependingProperty(final String name, final Context context) {
        return getSettings(context).getString(name + getAppVersionCode(context), null);
    }

    private static void saveVersionDependingProperty(final String name, final String value, final Context context) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putString(name + getAppVersionCode(context), value);
        editor.commit();
    }

    public static int getAppVersionCode(final Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                                    context.getPackageName(), 0);
            return pinfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e(AndlyticsApp.class.getSimpleName(), "unable to read version code", e);
        }
        return 0;
    }

    public static void saveChartTimeframe(final Timeframe value, final Context context) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putString(CHART_TIMEFRAME, value.name());
        editor.commit();
    }

    public static Timeframe getChartTimeframe(final Context activity) {
        return Timeframe.valueOf(getSettings(activity).getString(CHART_TIMEFRAME,
                                 Timeframe.LAST_THIRTY_DAYS.name()));
    }

    public static Boolean getChartSmooth(final Context context) {
        return getSettings(context).getBoolean(CHART_SMOOTH, true);
    }

    public static boolean getSkipAutologin(final Context context) {
        return getSettings(context).getBoolean(SKIP_AUTO_LOGIN, false);
    }

    public static void saveSkipAutoLogin(final Context context, final Boolean value) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putBoolean(SKIP_AUTO_LOGIN, value);
        editor.commit();
    }

    public static void saveStatsMode(final StatsMode value, final Context context) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putString(STATS_MODE, value.name());
        editor.commit();
    }

    public static StatsMode getStatsMode(final Context activity) {
        return StatsMode.valueOf(getSettings(activity).getString(STATS_MODE,
                                 StatsMode.PERCENT.name()));
    }

    public static boolean getNotificationPerf(final Context context, final String prefName) {
        return getSettings(context).getBoolean(prefName, true);
    }

    public static String getNotificationRingtone(final Context context) {
        return getSettings(context).getString(NOTIFICATION_RINGTONE, null);
    }

    public static Boolean getShowChartHint(final Context context) {
        return getSettings(context).getBoolean(SHOW_CHART_HINT, true);
    }

    public static void saveShowChartHint(final Context context, final Boolean value) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putBoolean(SHOW_CHART_HINT, value);
        editor.commit();
    }

    public static String getDateFormatStringShort(final Context context) {
        if (cachedDateFormatShort != null) {
            return cachedDateFormatShort;
        }
        String dateFormatStringLong = getDateFormatStringLong(context);
        // Build the short version by taking the long one and removing the year
        // We do this rather than using pre-defined short versions so that the
        // user
        // can use a default based on their locale
        String format = dateFormatStringLong.replace("yyyy", "").replace("yy", "");
        // Now go through the string removing any duplicate separators
        // and trimming leading/ending separators
        StringBuilder builder = new StringBuilder();
        char[] chars = format.toCharArray();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            char c = chars[i];
            if (Character.isLetter(c)) {
                // Always accept letters
                builder.append(c);
            } else if ((i > 0) && (i + 1 < length) && (c != chars[i + 1])) {
                // Only accept separators is they aren't duplicated
                // and aren't at the start or end
                builder.append(c);
            }
        }
        format = builder.toString();
        cachedDateFormatShort = format;
        return format;
    }

    /**
     * Clears the cached string representations used for date formatting Should
     * be called whenever the user preference changes
     */
    public static void clearCachedDateFormats() {
        cachedDateFormatShort = null;
        cachedDateFormatLong = null;
    }

    private static String getDateFormatStringLong(final Context context) {
        if (cachedDateFormatLong != null) {
            return cachedDateFormatLong;
        }
        String format = getSettings(context).getString(DATE_FORMAT_LONG, "DEFAULT");
        if ("DEFAULT".equals(format)) {
            format = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT)).toPattern();
            // Make it consistent with our pre-defined formats (always show
            // yyyy)
            format = format.replace("yyyy", "yy").replace("yy", "yyyy");
        }
        cachedDateFormatLong = format;
        return format;
    }

    @SuppressLint("SimpleDateFormat")
    public static DateFormat getDateFormatLong(final Context context) {
        return new SimpleDateFormat(getDateFormatStringLong(context));
    }

    @Deprecated
    public static void saveAdmobSiteId(final Context context, final String packageName, final String value) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putString(ADMOB_SITE_ID + packageName, value);
        editor.commit();
    }

    @Deprecated
    public static String getAdmobSiteId(final Context context, final String packageName) {
        return getSettings(context).getString(ADMOB_SITE_ID + packageName, null);
    }

    @Deprecated
    public static void saveAdmobAccount(final Context context, final String siteId, final String accountName) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putString(ADMOB_ACCOUNT + siteId, accountName);
        editor.commit();
    }

    @Deprecated
    public static String getAdmobAccount(final Context context, final String siteId) {
        return getSettings(context).getString(ADMOB_ACCOUNT + siteId, null);
    }

    public static int getLatestVersionCode(final Context context) {
        return getSettings(context).getInt(LATEST_VERSION_CODE, 0);
    }

    public static void saveLatestVersionCode(final Context context, final int latest) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putInt(LATEST_VERSION_CODE, latest);
        editor.commit();
    }

    public static String getRequestFeedback(final Context activity) {
        return getVersionDependingProperty(POST_REQUEST_FEEDBACK, activity);
    }

    public static void saveRequestFeedback(final Context activity, final String postdata) {
        saveVersionDependingProperty(POST_REQUEST_FEEDBACK, postdata, activity);
    }

    public static Timeframe getAdmobTimeframe(final Context context) {
        return Timeframe.valueOf(getSettings(context).getString(ADMOB_TIMEFRAME,
                                 Timeframe.LAST_THIRTY_DAYS.name()));
    }

    public static void saveAdmobTimeframe(final Timeframe value, final Context context) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putString(ADMOB_TIMEFRAME, value.name());
        editor.commit();
    }

    @Deprecated
    public static void saveIsHiddenAccount(final Context context, final String accountName, final Boolean hidden) {
        SharedPreferences.Editor editor = getSettings(context).edit();
        editor.putBoolean(HIDDEN_ACCOUNT + accountName, hidden);
        editor.commit();
    }

    @Deprecated
    public static boolean getIsHiddenAccount(final Context context, final String accountName) {
        return getSettings(context).getBoolean(HIDDEN_ACCOUNT + accountName, false);
    }

    public static boolean getHideAdmobForUnconfiguredApps(final Context context) {
        return getSettings(context).getBoolean(ADMOB_HIDE_FOR_UNCONFIGURED_APPS, false);
    }

    @Deprecated
    public static synchronized long getLastStatsRemoteUpdateTime(final Context activity,
            final String accountName) {
        return getSettings(activity).getLong(LAST_STATS_REMOTE_UPDATE + "." + accountName, 0);
    }

    @Deprecated
    public static synchronized void saveLastStatsRemoteUpdateTime(final Context activity,
            final String accountName, final long timestamp) {
        getSettings(activity).edit()
        .putLong(LAST_STATS_REMOTE_UPDATE + "." + accountName, timestamp).commit();
    }

    /**
     * Gets the last time that comments were updated for the given <b>packageName</b>
     *
     * @param activity
     * @param packageName
     * @return
     */
    @Deprecated
    public static synchronized long getLastCommentsRemoteUpdateTime(final Context activity,
            final String packageName) {
        return getSettings(activity).getLong(LAST_COMMENTS_REMOTE_UPDATE + "." + packageName, 0);
    }

    /**
     * Sets the time that comments were last updated for the given <b>packageName</b>
     *
     * @param activity
     * @param packageName
     * @param timestamp
     */
    @Deprecated
    public static synchronized void saveLastCommentsRemoteUpdateTime(final Context activity,
            final String packageName, final long timestamp) {
        getSettings(activity).edit()
        .putLong(LAST_COMMENTS_REMOTE_UPDATE + "." + packageName, timestamp).commit();
    }

    public static synchronized boolean isUseGoogleTranslateApp(final Context context) {
        return getSettings(context).getBoolean(USE_GOOGLE_TRANSLATE_APP, true);
    }

    public static synchronized void saveUseGoogleTranslateApp(final Context context, final boolean value) {
        getSettings(context).edit().putBoolean(USE_GOOGLE_TRANSLATE_APP, value).commit();
    }

    public static synchronized boolean isShowCommentAutoTranslations(final Context context) {
        return getSettings(context).getBoolean(SHOW_COMMENT_AUTO_TRANSLATIONS, true);
    }

    public static synchronized void saveShowCommentAutoTranslations(final Context context, final boolean value) {
        getSettings(context).edit().putBoolean(SHOW_COMMENT_AUTO_TRANSLATIONS, value).commit();
    }

    public static synchronized boolean isShowDeveloperCutRevenue(final Context context) {
        return getSettings(context).getBoolean(SHOW_DEVELOPER_CUT_REVENUE, true);
    }

    public static synchronized void saveShowDeveloperCutRevenue(final Context context, final boolean value) {
        getSettings(context).edit().putBoolean(SHOW_DEVELOPER_CUT_REVENUE, value).commit();
    }
}
