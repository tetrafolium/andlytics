package com.github.andlyticsproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.github.andlyticsproject.model.Link;
import java.util.ArrayList;
import java.util.List;

public class LinksListAdapter extends BaseAdapter {

private LayoutInflater layoutInflater;

private List<Link> links;

public LinksListAdapter(final AppInfoActivity activity) {
	this.layoutInflater = activity.getLayoutInflater();
	this.links = new ArrayList<Link>();
}

@Override
public View getView(final int position, final View convertView,
                    final ViewGroup parent) {

	final Link link = getItem(position);
	ViewHolderChild holder;

	if (convertView == null) {
		convertView =
			layoutInflater.inflate(R.layout.appinfo_links_list_item, null);

		holder = new ViewHolderChild();
		holder.name =
			(TextView)convertView.findViewById(R.id.appinfo_link_list_item_name);
		holder.url =
			(TextView)convertView.findViewById(R.id.appinfo_link_list_item_url);

		convertView.setTag(holder);
	} else {
		holder = (ViewHolderChild)convertView.getTag();
	}

	holder.name.setText(link.getName());
	holder.url.setText(link.getURL());

	return convertView;
}

static class ViewHolderChild {
TextView name;
TextView url;
}

public void setLinks(final List<Link> links) {
	this.links = links;
}

@Override
public int getCount() {
	return links.size();
}

@Override
public Link getItem(final int position) {
	return links.get(position);
}

@Override
public long getItemId(final int position) {
	return links.get(position).getId().longValue();
}
}
