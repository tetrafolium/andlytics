package com.github.andlyticsproject.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.github.andlyticsproject.R;
import com.github.andlyticsproject.util.DataLoader;
import java.io.IOException;

public class ChangelogFragment extends Fragment {
private static final String TAG = "ChangelogFragment";

@Override
public View onCreateView(final LayoutInflater inflater, final ViewGroup group,
                         final Bundle saved) {
	return inflater.inflate(R.layout.changelog, group, false);
}

@Override
public void onActivityCreated(final Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	WebView webView =
		(WebView)getActivity().findViewById(R.id.changelogcontent);

	try {
		webView.loadData(
			DataLoader.loadData(getActivity().getBaseContext(), "changelog"),
			"text/html", "UTF-8");
	} catch (IOException ioe) {
		Log.e(TAG, "Error reading changelog file!", ioe);
	}
}
}
