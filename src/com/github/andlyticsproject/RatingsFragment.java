package com.github.andlyticsproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import com.github.andlyticsproject.Preferences.Timeframe;
import com.github.andlyticsproject.chart.Chart.ChartSet;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.AppStatsSummary;
import com.github.andlyticsproject.model.StatsSummary;
import com.github.andlyticsproject.util.LoaderResult;

public class RatingsFragment extends ChartFragment<AppStats>
    implements LoaderManager.LoaderCallbacks<LoaderResult<AppStatsSummary>> {

  private static final String TAG = RatingsFragment.class.getSimpleName();

  public RatingsFragment() {}

  @Override
  public ChartSet getChartSet() {
    return ChartSet.RATINGS;
  }

  @Override
  public String getTitle() {
    // this can be called before the fragment is attached
    Context ctx = AndlyticsApp.getInstance();
    return ctx.getString(R.string.ratings);
  }

  @Override
  public Loader<LoaderResult<AppStatsSummary>>
  onCreateLoader(final int id, final Bundle args) {
    String packageName = null;
    Timeframe timeframe = null;
    boolean smoothEnabled = false;
    if (args != null) {
      packageName = args.getString(AppStatsSummaryLoader.ARG_PACKAGE_NAME);
      timeframe =
          (Timeframe)args.getSerializable(AppStatsSummaryLoader.ARG_TIMEFRAME);
      smoothEnabled = args.getBoolean(AppStatsSummaryLoader.ARG_SMOOTH_ENABLED);
    }

    return new AppStatsSummaryLoader(getActivity(), packageName, timeframe,
                                     smoothEnabled);
  }

  @Override
  public void onLoadFinished(final Loader<LoaderResult<AppStatsSummary>> loader,
                             final LoaderResult<AppStatsSummary> result) {
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
  public void
  onLoaderReset(final Loader<LoaderResult<AppStatsSummary>> loader) {}

  @Override
  public void initLoader(final Bundle args) {
    getLoaderManager().initLoader(0, args, this);
  }

  @Override
  public void restartLoader(final Bundle args) {
    getLoaderManager().restartLoader(0, args, this);
  }

  @Override
  public ChartListAdapter<AppStats> createChartAdapter() {
    return new RatingsChartListAdapter(getActivity());
  }

  @Override
  public void setupListAdapter(final ChartListAdapter<AppStats> listAdapter,
                               final StatsSummary<AppStats> statsSummary) {
    RatingsChartListAdapter adapter = (RatingsChartListAdapter)listAdapter;
    AppStatsSummary summary = (AppStatsSummary)statsSummary;
    adapter.setHighestRatingChange(summary.getHighestRatingChange());
    adapter.setLowestRatingChange(summary.getLowestRatingChange());
  }
}
