package com.github.andlyticsproject.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.andlyticsproject.R;

public class LongTextDialog extends DialogFragment {
public LongTextDialog() {
	setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog);
}

@Override
public View onCreateView(final LayoutInflater inflater,
                         final ViewGroup container,
                         final Bundle savedInstanceState) {
	Bundle arguments = getArguments();

	View view = inflater.inflate(R.layout.longtext_dialog, container);

	TextView longText = (TextView)view.findViewById(R.id.longtext_tv);

	TextView longTextTitle = (TextView)view.findViewById(R.id.longtext_title);

	if (arguments.containsKey("longText")) {
		longText.setText(Html.fromHtml(arguments.getString("longText")));
	}

	if (arguments.containsKey("title")) {
		longTextTitle.setText(arguments.getInt("title"));
	}

	view.findViewById(R.id.longtext_dialog_dismiss)
	.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
			        dismiss();
			}
		});

	return view;
}
}
