package com.github.andlyticsproject.io;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.github.andlyticsproject.ContentAdapter;
import com.github.andlyticsproject.LoginActivity;
import com.github.andlyticsproject.R;
import com.github.andlyticsproject.model.AppStats;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ImportService extends IntentService {

private static final String TAG = ImportService.class.getSimpleName();

public static final int NOTIFICATION_ID_PROGRESS = 2;

public static final int NOTIFICATION_ID_FINISHED = 2;

public static final String FILE_NAMES = "fileNames";

public static final String ACCOUNT_NAME = "accountName";

private boolean errors = false;

private String accountName;

private List<String> fileNames;

private Uri zipFileUri;

private NotificationManager notificationManager;

private Exception error;

public ImportService() {
	super("andlytics ImportService");
}

@Override
public void onCreate() {
	super.onCreate();

	notificationManager =
		(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
}

@Override
protected void onHandleIntent(final Intent intent) {
	Log.d(TAG, "import service onStartCommand");

	this.zipFileUri = intent.getData();
	Log.d(TAG, "zip file: " + zipFileUri);

	this.fileNames = Arrays.asList(intent.getStringArrayExtra(FILE_NAMES));
	Log.d(TAG, "file names:: " + fileNames);

	this.accountName = intent.getStringExtra(ACCOUNT_NAME);
	Log.d(TAG, "account name:: " + accountName);

	boolean success = importStats();
	notifyImportFinished(success);
}

private boolean importStats() {
	String message = getApplicationContext().getString(R.string.import_started);
	sendNotification(message);

	ContentAdapter db = ContentAdapter.getInstance(getApplication());
	try {
		StatsCsvReaderWriter statsWriter =
			new StatsCsvReaderWriter(ImportService.this);

		ZipInputStream inzip =
			new ZipInputStream(getContentResolver().openInputStream(zipFileUri));
		ZipEntry entry = null;
		while ((entry = inzip.getNextEntry()) != null) {
			String filename = entry.getName();
			if (!fileNames.contains(filename)) {
				continue;
			}

			List<AppStats> stats = statsWriter.readStats(inzip);
			if (!stats.isEmpty()) {
				String packageName = stats.get(0).getPackageName();
				message = getApplicationContext().getString(R.string.importing) +
				          " " + packageName;
				sendNotification(message);
				for (AppStats appStats : stats)
					db.insertOrUpdateAppStats(appStats, packageName);
			}
		}

		inzip.close();
	} catch (Exception e) {
		Log.e(TAG, "Error importing stats: " + e.getMessage(), e);
		error = e;
		errors = true;
	}

	message = getResources().getString(R.string.app_name) + ": " +
	          getApplicationContext().getString(R.string.import_finished);
	sendNotification(message);

	return !errors;
}

private void notifyImportFinished(final boolean success) {
	notificationManager.cancel(NOTIFICATION_ID_PROGRESS);

	Intent startActivityIntent = new Intent(this, LoginActivity.class);
	startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	PendingIntent pendingIntent = PendingIntent.getActivity(
		getApplicationContext(), 0, startActivityIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);

	Builder builder = new NotificationCompat.Builder(getApplicationContext());
	builder.setSmallIcon(R.drawable.statusbar_andlytics);
	builder.setContentIntent(pendingIntent);
	builder.setWhen(System.currentTimeMillis());
	builder.setDefaults(Notification.DEFAULT_ALL);
	builder.setAutoCancel(true);
	builder.setOngoing(false);

	if (success) {
		String title =
			getResources().getString(R.string.app_name) + ": " +
			getApplicationContext().getString(R.string.import_finished);
		String message =
			getResources().getString(R.string.imported_apps, fileNames.size());
		builder.setContentTitle(title);
		builder.setContentText(message);
		BigTextStyle style = new BigTextStyle(builder);
		style.setBigContentTitle(title);
		style.bigText(message);
		style.setSummaryText(accountName);
		builder.setStyle(style);
	} else {
		String title = getResources().getString(R.string.app_name) + ": " +
		               getApplicationContext().getString(R.string.import_error);
		String message = error.getMessage();
		builder.setContentTitle(title);
		builder.setContentText(message);
		BigTextStyle style = new BigTextStyle(builder);
		style.setBigContentTitle(title);
		style.bigText(message);
		style.setSummaryText(accountName);
		builder.setStyle(style);
	}

	notificationManager.notify(NOTIFICATION_ID_FINISHED, builder.build());
}

/**
 * Send a notification to the progress bar.
 */
protected void sendNotification(final String message) {
	Intent startActivityIntent = new Intent();
	PendingIntent pendingIntent = PendingIntent.getActivity(
		getApplicationContext(), 0, startActivityIntent, 0);

	Builder builder = new NotificationCompat.Builder(getApplicationContext());
	builder.setSmallIcon(R.drawable.statusbar_andlytics);
	builder.setContentTitle(
		getResources().getString(R.string.app_name) + ": " +
		getApplicationContext().getString(R.string.import_));
	builder.setContentText(message);
	builder.setContentIntent(pendingIntent);
	builder.setDefaults(0);
	builder.setAutoCancel(true);
	builder.setOngoing(true);

	notificationManager.notify(NOTIFICATION_ID_PROGRESS, builder.build());
}
}
