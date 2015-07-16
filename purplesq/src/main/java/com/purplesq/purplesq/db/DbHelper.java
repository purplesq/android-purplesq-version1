package com.purplesq.purplesq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.purplesq.purplesq.db.PsqContract.PurchaseHistoryTable;
import com.purplesq.purplesq.db.PsqContract.UserAuthTable;
import com.purplesq.purplesq.db.PsqContract.UserProfileTable;

/**
 * Created by nishant on 13/07/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PSQ_Database.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_USER_PROFILE =
            "CREATE TABLE " + UserProfileTable.TABLE_NAME + " (" +
                    UserProfileTable._ID + " INTEGER PRIMARY KEY," +
                    UserProfileTable.COL_USER_ID + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_FIRSTNAME + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_LASTNAME + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_PHONE + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_DOB + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_GENDER + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_INSTITUTE + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_IMAGEURL + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_ROLE + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_STATUS_ACCOUNT + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_STATUS_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserProfileTable.COL_STATUS_PHONE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_USER_PROFILE =
            "DROP TABLE IF EXISTS " + UserProfileTable.TABLE_NAME;


    private static final String SQL_CREATE_USER_AUTH =
            "CREATE TABLE " + UserAuthTable.TABLE_NAME + " (" +
                    UserAuthTable._ID + " INTEGER PRIMARY KEY," +
                    UserAuthTable.COL_TOKEN + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_USER_ID + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_FIRSTNAME + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_LASTNAME + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_PHONE + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_INSTITUTE + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_IMAGEURL + TEXT_TYPE + COMMA_SEP +
                    UserAuthTable.COL_EXPIRY + LONG_TYPE + COMMA_SEP +
                    UserAuthTable.COL_EXPIRY_TIME + LONG_TYPE +
                    " )";

    private static final String SQL_DELETE_USER_AUTH =
            "DROP TABLE IF EXISTS " + UserAuthTable.TABLE_NAME;


    private static final String SQL_CREATE_PURCHASE_HISTORY =
            "CREATE TABLE " + PurchaseHistoryTable.TABLE_NAME + " (" +
                    PurchaseHistoryTable._ID + " INTEGER PRIMARY KEY," +
                    PurchaseHistoryTable.COL_TRANS_ID + TEXT_TYPE + COMMA_SEP +
                    PurchaseHistoryTable.COL_PURCHASE_JSON + TEXT_TYPE + COMMA_SEP +
                    " UNIQUE (" + PurchaseHistoryTable.COL_TRANS_ID + ")" +
                    " )";

    private static final String SQL_DELETE_PURCHASE_HISTORY =
            "DROP TABLE IF EXISTS " + PurchaseHistoryTable.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_PROFILE);
        db.execSQL(SQL_CREATE_USER_AUTH);
        db.execSQL(SQL_CREATE_PURCHASE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USER_PROFILE);
        db.execSQL(SQL_DELETE_USER_AUTH);
        db.execSQL(SQL_DELETE_PURCHASE_HISTORY);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
