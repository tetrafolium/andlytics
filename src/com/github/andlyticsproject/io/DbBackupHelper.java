package com.github.andlyticsproject.io;

import android.app.backup.FileBackupHelper;
import android.content.Context;

public class DbBackupHelper extends FileBackupHelper {

    public DbBackupHelper(final Context ctx, final String dbName) {
        super(ctx, "../databases/" + dbName);
    }
}
