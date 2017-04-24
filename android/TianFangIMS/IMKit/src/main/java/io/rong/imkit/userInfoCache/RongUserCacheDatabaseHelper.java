package io.rong.imkit.userInfoCache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

class RongUserCacheDatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "IMKitUserInfoCache";
    private final static int DB_VERSION = 1;
    private static String dbPath;

    private SQLiteDatabase database;

    public RongUserCacheDatabaseHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    private RongUserCacheDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(new RongDatabaseContext(context, dbPath), name, factory, version);
    }

    public static void setDbPath (Context context, String appKey, String currentUserId) {
        dbPath = context.getFilesDir().getAbsolutePath();
        dbPath = dbPath + File.separator + appKey + File.separator + currentUserId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        database = db;

        db.execSQL("CREATE TABLE users (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS id_idx_users ON users(id)");

        db.execSQL("CREATE TABLE group_users (group_id TEXT NOT NULL, user_id TEXT NOT NULL, nickname TEXT)");
        db.execSQL("CREATE TABLE groups (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
        db.execSQL("CREATE TABLE discussions (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
//        db.execSQL("CREATE TABLE public_service_profiles (type TEXT NOT NULL, id TEXT NOT NULL, name TEXT, portrait TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
