
package com.github.andlyticsproject.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;

@SuppressLint("NewApi")
public class MediaScannerWrapper {

  private MediaScannerWrapper() {}

  public static void scanFile(final Context ctx, final String filename) {
    MediaScannerConnection.scanFile(ctx, new String[] {filename}, null, null);
  }
}
