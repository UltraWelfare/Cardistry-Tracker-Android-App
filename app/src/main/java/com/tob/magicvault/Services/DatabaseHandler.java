package com.tob.magicvault.Services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tob.magicvault.Services.Contracts.MagicTrickContract;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "magicvault.db";

    private static DatabaseHandler _instance = null;

    public static DatabaseHandler get(Context context) {
        if (_instance == null) _instance = new DatabaseHandler(context.getApplicationContext());
        return _instance;
    }


    private static final String SQL_CREATE_MAGIC_TRICKS =
            "CREATE TABLE " + MagicTrickContract.MagicTricks.TABLE_NAME + " (" +
                    MagicTrickContract.MagicTricks._ID + " INTEGER PRIMARY KEY," +
                    MagicTrickContract.MagicTricks.COLUMN_NAME_TRICK_NAME + " TEXT UNIQUE," +
                    MagicTrickContract.MagicTricks.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    MagicTrickContract.MagicTricks.COLUMN_NAME_OWNER + " TEXT," +
                    MagicTrickContract.MagicTricks.COLUMN_NAME_IMAGE_URI + " TEXT," +
                    MagicTrickContract.MagicTricks.COLUMN_NAME_DIFFICULTY + " TEXT," +
                    MagicTrickContract.MagicTricks.COLUMN_NAME_CATEGORY + " ΤΕΧΤ)";

    private static final String SQL_CREATE_MAGIC_TRICK_STEPS =
            "CREATE TABLE " + MagicTrickContract.MagicTrickSteps.TABLE_NAME + " (" +
                    MagicTrickContract.MagicTrickSteps._ID + " INTEGER PRIMARY KEY," +
                    MagicTrickContract.MagicTrickSteps.COLUMN_NAME_STEP_DESCRIPTION + " TEXT," +
                    MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK + " INTEGER," +
                    "FOREIGN KEY(" + MagicTrickContract.MagicTrickSteps.COLUMN_NAME_ID_MAGIC_TRICK + ") REFERENCES " + MagicTrickContract.MagicTricks.TABLE_NAME + "(" + MagicTrickContract.MagicTricks._ID + ")" + ")";

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "Creating database");
        db.execSQL(SQL_CREATE_MAGIC_TRICKS);
        db.execSQL(SQL_CREATE_MAGIC_TRICK_STEPS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Fix sometime
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO : Fix sometime
    }
}
