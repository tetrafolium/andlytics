package com.github.andlyticsproject.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.github.andlyticsproject.io.MediaScannerWrapper;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

private static final String TAG = FileUtils.class.getSimpleName();

private FileUtils() {
}

public static void writeToFile(final File file, final String str) {
	try {
		FileOutputStream out = new FileOutputStream(file);
		out.write(str.getBytes("UTF-8"));
		out.flush();
		FileUtils.closeSilently(out);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}

public static void writeToExternalStorage(final String filename,
                                          final String str) {
	writeToFile(new File(Environment.getExternalStorageDirectory(), filename),
	            str);
}

public static void writeToAndlyticsDir(final String filename,
                                       final String str) {
	File andlyticsDir = getAndlyticsDir();

	writeToFile(new File(andlyticsDir, filename), str);
}

private static File getAndlyticsDir() {
	File andlyticsDir =
		new File(Environment.getExternalStorageDirectory(), "andlytics");
	if ((!andlyticsDir.exists()) && (!andlyticsDir.mkdirs())) {
		throw new RuntimeException("Couldn't create: " +
		                           andlyticsDir.getAbsolutePath());
	}
	return andlyticsDir;
}

public static void writeToDebugDir(final String filename, final String str) {
	File andlyticsDir = getAndlyticsDir();
	File debugDir = new File(andlyticsDir, "debug");
	if ((!debugDir.exists()) && (!debugDir.mkdirs())) {
		throw new RuntimeException("Couldn't create: " +
		                           debugDir.getAbsolutePath());
	}

	writeToFile(new File(debugDir, filename), str);
}

public static void tryWriteToDebugDir(final String filename,
                                      final String str) {
	try {
		writeToDebugDir(filename, str);
	} catch (Exception e) {
		Log.w(TAG, "Error writing debug file: " + filename, e);
	}
}

public static String readFileAsString(final String filename) {
	FileInputStream in = null;
	try {
		in = new FileInputStream(filename);
		byte[] data = new byte[in.available()];
		in.read(data);

		return new String(data, "UTF-8");
	} catch (IOException e) {
		throw new RuntimeException(e);
	} finally {
		if (in != null) {
			FileUtils.closeSilently(in);
		}
	}
}

public static byte[] readFromUri(final Context ctx, final Uri uri) {
	InputStream in = null;
	try {
		in = ctx.getContentResolver().openInputStream(uri);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int count = -1;
		while ((count = in.read(buff)) != -1) {
			baos.write(buff, 0, count);
		}

		return baos.toByteArray();
	} catch (IOException e) {
		throw new RuntimeException(e);
	} finally {
		if (in != null) {
			FileUtils.closeSilently(in);
		}
	}
}

public static void closeSilently(final Closeable c) {
	if (c != null) {
		try {
			c.close();
		} catch (Exception e) {
		}
	}
}

public static void scanFile(final Context ctx, final String filename) {
	if (Utils.isFroyo()) {
		MediaScannerWrapper.scanFile(ctx, filename);
	}
}
}
