
package com.github.andlyticsproject.sync;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class ContentProvider extends android.content.ContentProvider {

@Override
public boolean onCreate() {
	return false;
}

@Override
public Cursor query(final Uri uri, final String[] projection,
                    final String selection, final String[] selectionArgs,
                    final String sortOrder) {
	return null;
}

@Override
public String getType(final Uri uri) {
	return null;
}

@Override
public Uri insert(final Uri uri, final ContentValues values) {
	return null;
}

@Override
public int delete(final Uri uri, final String selection,
                  final String[] selectionArgs) {
	return 0;
}

@Override
public int update(final Uri uri, final ContentValues values,
                  final String selection, final String[] selectionArgs) {
	return 0;
}
}
