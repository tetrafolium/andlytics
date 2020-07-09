package com.github.andlyticsproject;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.github.andlyticsproject.db.AndlyticsDb;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(sharedPreferencesMode = Context.MODE_PRIVATE, sharedPreferencesName = Preferences.PREF, mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.crash_toast, sendReportsInDevMode = false)
public class AndlyticsApp extends Application {

    private static final String TAG = AndlyticsApp.class.getSimpleName();

    private ContentAdapter db;

    private static AndlyticsApp sInstance;

    private boolean isAppVisible = false;

    @Override
    public void onCreate() {
        super.onCreate();

        initAcra();

        // open DB here to  force schema and preferences migration
        // the DB is kept open through the lifecycle ot the app, no need to
        // close here
        AndlyticsDb.getInstance(getApplicationContext()).getWritableDatabase();

        setDbAdapter(ContentAdapter.getInstance(this));
        sInstance = this;
    }

    private void initAcra() {
        try {
            ACRA.init(this);
            String bugsenseUrl = getResources().getString(R.string.bugsense_url);
            HttpSender bugSenseSender = new HttpSender(HttpSender.Method.POST,
                    HttpSender.Type.FORM, bugsenseUrl, null);
            ACRA.getErrorReporter().setReportSender(bugSenseSender);
        } catch (IllegalStateException e) {
            Log.w(TAG, "ACRA.init() called more than once?: " + e.getMessage(), e);
        }
    }

    public boolean isDebug() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0;
    }

    public static AndlyticsApp getInstance() {
        return sInstance;
    }

    public boolean isAppVisible() {
        // TODO This is a bit of a hack, could it be improved
        return isAppVisible;
    }

    public void setIsAppVisible(boolean isVisible) {
        isAppVisible = isVisible;
    }

    public void setDbAdapter(ContentAdapter db) {
        this.db = db;
    }

    public ContentAdapter getDbAdapter() {
        return db;
    }

}
