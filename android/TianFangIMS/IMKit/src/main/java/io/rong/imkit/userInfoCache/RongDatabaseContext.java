package io.rong.imkit.userInfoCache;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by lifei on 16/5/17.
 */
public class RongDatabaseContext extends ContextWrapper {

    private String mDirPath;

    public RongDatabaseContext(Context context, String dirPath) {
        super(context);
        this.mDirPath = dirPath;
    }

    @Override
    public File getDatabasePath(String name) {
        File result = new File(mDirPath + File.separator + name);

        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory, errorHandler);
    }
}
