
package com.github.andlyticsproject.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

public class ChartGalleryAdapter extends BaseAdapter {

private List<View> views;

public ChartGalleryAdapter(final List<View> views) {
	this.setViews(views);
}

public View getView(final int position, final View convertView,
                    final ViewGroup parent) {
	return getItem(position);
}

@Override
public int getCount() {
	return getViews().size();
}

@Override
public View getItem(final int position) {
	return getViews().get(position);
}

@Override
public long getItemId(final int position) {
	return position;
}

public void setViews(final List<View> views) {
	this.views = views;
}

public List<View> getViews() {
	return views;
}
}
