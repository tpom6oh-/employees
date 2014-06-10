package com.tpom6oh.employees.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.tpom6oh.employees.BuildConfig;
import com.tpom6oh.employees.model.employee.EmployeeColumns;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = DbOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "employees.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;
    private final DbOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    private static final String SQL_CREATE_TABLE_EMPLOYEE = "CREATE TABLE IF NOT EXISTS "
            + EmployeeColumns.TABLE_NAME + " ( "
            + EmployeeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EmployeeColumns.NAME + " TEXT NOT NULL, "
            + EmployeeColumns.DIVISION + " TEXT NOT NULL, "
            + EmployeeColumns.SALARY + " INTEGER NOT NULL, "
            + EmployeeColumns.COMPANY + " TEXT NOT NULL, "
            + EmployeeColumns.EMPLOYEMENT_YEAR + " INTEGER NOT NULL, "
            + EmployeeColumns.EMPLOYEMENT_DATE + " INTEGER NOT NULL, "
            + EmployeeColumns.COUNTRY_ID + " INTEGER NOT NULL, "
            + EmployeeColumns.COUNTRY_NAME + " TEXT, "
            + EmployeeColumns.ENTERPRISE + " TEXT NOT NULL, "
            + EmployeeColumns.IMAGE_URL + " TEXT "
            + " );";

    // @formatter:on

    public static DbOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */

    private static DbOpenHelper newInstancePreHoneycomb(Context context) {
        return new DbOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    private DbOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mOpenHelperCallbacks = new DbOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static DbOpenHelper newInstancePostHoneycomb(Context context) {
        return new DbOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private DbOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new DbOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_EMPLOYEE);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
