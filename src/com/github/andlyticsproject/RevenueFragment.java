package com.github.andlyticsproject;


import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.github.andlyticsproject.Preferences.Timeframe;
import com.github.andlyticsproject.chart.Chart.ChartSet;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.AppStatsSummary;
import com.github.andlyticsproject.model.StatsSummary;
import com.github.andlyticsproject.util.LoaderResult;

public class RevenueFragment extends ChartFragment<AppStats> implements
    LoaderManager.LoaderCallbacks<LoaderResult<AppStatsSummary>> {

    private static final String TAG = RevenueFragment.class.getSimpleName();

    public RevenueFragment() {
    }

    @Override
    public ChartSet getChartSet() {
        return ChartSet.REVENUE;
    }

    @Override
    public String getTitle() {
        // this can be called before the fragment is attached
        Context ctx = AndlyticsApp.getInstance();
        return ctx.getString(R.string.revenue);
    }

    @Override
    public Loader<LoaderResult<AppStatsSummary>> onCreateLoader(int id, Bundle args) {
        String packageName = null;
        Timeframe timeframe = null;
        boolean smoothEnabled = false;
        if (args != null) {
            packageName = args.getString(AppStatsSummaryLoader.ARG_PACKAGE_NAME);
            timeframe = (Timeframe) args.getSerializable(AppStatsSummaryLoader.ARG_TIMEFRAME);
            smoothEnabled = args.getBoolean(AppStatsSummaryLoader.ARG_SMOOTH_ENABLED);
        }

        return new AppStatsSummaryLoader(getActivity(), packageName, timeframe, smoothEnabled);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<AppStatsSummary>> loader,
                               LoaderResult<AppStatsSummary> result) {
        if (getActivity() == null) {
            return;
        }

        statsActivity.refreshFinished();

        if (result.isFailed()) {
            Log.e(TAG, "Error fetching chart data: " + result.getError().getMessage(),
                  result.getError());
            statsActivity.handleUserVisibleException(result.getError());

            return;
        }

        if (result.getData() == null) {
            return;
        }

        AppStatsSummary statsSummary = result.getData();
        if (statsSummary != null) {
            updateView(statsSummary);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<AppStatsSummary>> loader) {
    }

    @Override
    public void initLoader(Bundle args) {
        getLoaderManager().initLoader(0, args, this);

    }

    @Override
    public void restartLoader(Bundle args) {
        getLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public ChartListAdapter<AppStats> createChartAdapter() {
        return new RevenueChartListAdapter(getActivity());
    }

    @Override
    public void setupListAdapter(ChartListAdapter<AppStats> listAdapter,
                                 StatsSummary<AppStats> statsSummary) {
        // nothing to do
    }

}
