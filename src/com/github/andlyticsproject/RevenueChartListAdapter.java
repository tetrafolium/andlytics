package com.github.andlyticsproject;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.github.andlyticsproject.chart.Chart;
import com.github.andlyticsproject.chart.Chart.ValueCallbackHander;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.Revenue;
import com.github.andlyticsproject.util.Utils;
import java.util.List;

public class RevenueChartListAdapter extends ChartListAdapter<AppStats> {

private static final int COL_REVENUE = 1;
private static final int COL_DEVELOPER_CUT = 2;

public RevenueChartListAdapter(final Activity activity) {
	super(activity);
}

@Override
public int getNumPages() {
	return 1;
}

@Override
public int getNumCharts(final int page) throws IndexOutOfBoundsException {
	switch (page) {
	case 0:
		return 3;
	}
	throw new IndexOutOfBoundsException("page=" + page);
}

@Override
public String getChartTitle(final int page, final int column)
throws IndexOutOfBoundsException {
	if (column == COL_DATE) {
		return "";
	}

	switch (page) {
	case 0:
		switch (column) {
		case COL_REVENUE:
			return activity.getString(R.string.total_revenue);
		case COL_DEVELOPER_CUT:
			return "Developer cut";
		}
	}
	throw new IndexOutOfBoundsException("page=" + page + " column=" + column);
}

@Override
public void updateChartValue(final int position, final int page,
                             final int column, final TextView tv)
throws IndexOutOfBoundsException {
	AppStats appStats = getItem(position);
	Revenue totalRevenue = appStats.getTotalRevenue();
	if (column == COL_DATE) {
		tv.setText(dateFormat.format(appStats.getDate()));
		return;
	}
	int textColor = BLACK_TEXT;
	switch (page) {
	case 0: {

		switch (column) {
		case COL_REVENUE:
			tv.setText(Utils.safeToString(totalRevenue));
			tv.setTextColor(textColor);
			return;
		case COL_DEVELOPER_CUT:
			if (totalRevenue == null) {
				tv.setText("");
			} else {
				tv.setText(totalRevenue.developerCutAsString());
			}
			tv.setTextColor(textColor);
			return;
		}
	}
	}
	throw new IndexOutOfBoundsException("page=" + page + " column=" + column);
}

public View buildChart(final Context context, final Chart baseChart,
                       final List<?> statsForApp, final int page,
                       final int column) throws IndexOutOfBoundsException {
	ValueCallbackHander handler = null;
	switch (page) {
	case 0:
		switch (column) {
		case COL_REVENUE:
			handler = new DevConValueCallbackHander() {
				@Override
				public double getValue(final Object appInfo) {
					AppStats stats = (AppStats)appInfo;
					return stats.getTotalRevenue() == null
		? 0
		: stats.getTotalRevenue().getAmount();
				}
			};
			return baseChart.buildLineChart(context, statsForApp.toArray(),
			                                handler);
		case COL_DEVELOPER_CUT:
			handler = new DevConValueCallbackHander() {
				@Override
				public double getValue(final Object appInfo) {
					AppStats stats = (AppStats)appInfo;
					return stats.getTotalRevenue() == null
		? 0
		: stats.getTotalRevenue().getDeveloperCut();
				}
			};
			return baseChart.buildLineChart(context, statsForApp.toArray(),
			                                handler);
		}
	}

	throw new IndexOutOfBoundsException("page=" + page + " column=" + column);
}

@Override
public String getSubHeadLine(final int page, final int column)
throws IndexOutOfBoundsException {
	if (column == COL_DATE) {
		return "";
	}
	switch (page) {
	case 0:
		Preferences.saveShowChartHint(activity, false);
		if (overallStats == null) {
			return "";
		}

		switch (column) {
		case COL_REVENUE:
			return overallStats.getTotalRevenue() == null
	    ? "unknown"
	    : overallStats.getTotalRevenue().asString();
		case COL_DEVELOPER_CUT:
			return overallStats.getTotalRevenue() == null
	    ? "unknown"
	    : overallStats.getTotalRevenue().developerCutAsString();
		}
	}
	throw new IndexOutOfBoundsException("page=" + page + " column=" + column);
}

@Override
public AppStats getItem(final int position) {
	return getStats().get(position);
}

@Override
protected boolean isSmothValue(final int page, final int position) {
	return page == 0 && getItem(position).isSmoothingApplied();
}
}
