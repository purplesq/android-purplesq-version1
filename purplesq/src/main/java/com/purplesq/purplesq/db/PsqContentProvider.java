package com.purplesq.purplesq.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.purplesq.purplesq.db.PsqContract.PurchaseHistoryTable;
import com.purplesq.purplesq.db.PsqContract.UserAuthTable;
import com.purplesq.purplesq.db.PsqContract.UserProfileTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nishant on 13/07/15.
 */
public class PsqContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.purplesq.purplesq.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    private static final String PATH_USER_PROFILE = "userProfile";
    private static final String PATH_USER_AUTH = "userAuth";
    private static final String PATH_PURCHASE_HISTORY = "purchaseHistory";

    public static final Uri URI_USER_PROFILE = Uri.parse("content://" + AUTHORITY + "/" + PATH_USER_PROFILE);
    public static final Uri URI_USER_AUTH = Uri.parse("content://" + AUTHORITY + "/" + PATH_USER_AUTH);
    public static final Uri URI_PURCHASE_HISTORY = Uri.parse("content://" + AUTHORITY + "/" + PATH_PURCHASE_HISTORY);

    private static final int USER_PROFILE = 1000;
    private static final int USER_PROFILE_ID = 1001;

    private static final int USER_AUTH = 2000;
    private static final int USER_AUTH_ID = 2001;

    private static final int PURCHASE_HISTORY = 3000;
    private static final int PURCHASE_HISTORY_ID = 3001;

    private static final String USER_PROFILE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_USER_PROFILE;
    private static final String USER_PROFILE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_USER_PROFILE;

    private static final String USER_AUTH_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_USER_AUTH;
    private static final String USER_AUTH_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_USER_AUTH;

    private static final String PURCHASE_HISTORY_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_PURCHASE_HISTORY;
    private static final String PURCHASE_HISTORY_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_PURCHASE_HISTORY;


    private static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, PATH_USER_PROFILE, USER_PROFILE);
        matcher.addURI(AUTHORITY, PATH_USER_PROFILE + "/#", USER_PROFILE_ID);

        matcher.addURI(AUTHORITY, PATH_USER_AUTH, USER_AUTH);
        matcher.addURI(AUTHORITY, PATH_USER_AUTH + "/#", USER_AUTH_ID);

        matcher.addURI(AUTHORITY, PATH_PURCHASE_HISTORY, PURCHASE_HISTORY);
        matcher.addURI(AUTHORITY, PATH_PURCHASE_HISTORY + "/#", PURCHASE_HISTORY_ID);

    }

    DBHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return (dbHelper != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = matcher.match(uri);
        String tableName = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (match) {
            case USER_PROFILE_ID:
                tableName = UserProfileTable.TABLE_NAME;
                qb.appendWhere(UserProfileTable._ID + "=" + uri.getLastPathSegment());
                break;
            case USER_PROFILE:
                tableName = UserProfileTable.TABLE_NAME;
                break;
            case USER_AUTH_ID:
                tableName = UserAuthTable.TABLE_NAME;
                qb.appendWhere(UserAuthTable._ID + "=" + uri.getLastPathSegment());
                break;
            case USER_AUTH:
                tableName = UserAuthTable.TABLE_NAME;
                break;
            case PURCHASE_HISTORY_ID:
                tableName = PurchaseHistoryTable.TABLE_NAME;
                qb.appendWhere(PurchaseHistoryTable._ID + "=" + uri.getLastPathSegment());
                break;
            case PURCHASE_HISTORY:
                tableName = PurchaseHistoryTable.TABLE_NAME;
                break;
        }

        if (tableName == null) {
            throw new SQLException("Invalid URI " + uri);
        }

        qb.setTables(tableName);
        Cursor c = qb.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case USER_PROFILE_ID:
                return USER_PROFILE_CONTENT_ITEM_TYPE;
            case USER_PROFILE:
                return USER_PROFILE_CONTENT_TYPE;
            case USER_AUTH_ID:
                return USER_AUTH_CONTENT_ITEM_TYPE;
            case USER_AUTH:
                return USER_AUTH_CONTENT_TYPE;
            case PURCHASE_HISTORY_ID:
                return PURCHASE_HISTORY_CONTENT_ITEM_TYPE;
            case PURCHASE_HISTORY:
                return PURCHASE_HISTORY_CONTENT_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        String tableName = getTable(uri);

        long rowId = sqlDB.insert(tableName, null, values);

        if (rowId == 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs) {
        final int match = matcher.match(uri);
        int rowsAffected = 0;
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        switch (match) {

            case USER_PROFILE_ID:
                String userProfileId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, UserProfileTable._ID + "=" + userProfileId, null);
                } else {
                    rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, where + " AND " + UserProfileTable._ID + "=" + userProfileId, selectionArgs);
                }
                break;
            case USER_PROFILE:
                rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, where, selectionArgs);
                break;
            case USER_AUTH_ID:
                String userAuthId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, UserProfileTable._ID + "=" + userAuthId, null);
                } else {
                    rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, where + " AND " + UserProfileTable._ID + "=" + userAuthId, selectionArgs);
                }
                break;
            case USER_AUTH:
                rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, where, selectionArgs);
                break;
            case PURCHASE_HISTORY_ID:
                String purchaseHistoryId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, UserProfileTable._ID + "=" + purchaseHistoryId, null);
                } else {
                    rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, where + " AND " + UserProfileTable._ID + "=" + purchaseHistoryId, selectionArgs);
                }
                break;
            case PURCHASE_HISTORY:
                rowsAffected = sqlDB.delete(UserProfileTable.TABLE_NAME, where, selectionArgs);
                break;

            default:
                throw new SQLException("Failed to delete row " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = matcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsAffected = 0;
        switch (match) {

            case USER_PROFILE_ID:
                String profileId = uri.getLastPathSegment();
                StringBuilder profileSelection = new StringBuilder(UserProfileTable._ID + "=" + profileId);
                if (!TextUtils.isEmpty(selection)) {
                    profileSelection.append(" AND " + selection);
                }
                rowsAffected = sqlDB.update(UserProfileTable.TABLE_NAME, values, profileSelection.toString(), null);
                break;
            case USER_PROFILE:
                rowsAffected = sqlDB.update(UserProfileTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_AUTH_ID:
                String userAuthId = uri.getLastPathSegment();
                StringBuilder userAuthSelection = new StringBuilder(UserProfileTable._ID + "=" + userAuthId);
                if (!TextUtils.isEmpty(selection)) {
                    userAuthSelection.append(" AND " + selection);
                }
                rowsAffected = sqlDB.update(UserProfileTable.TABLE_NAME, values, userAuthSelection.toString(), null);
                break;
            case USER_AUTH:
                rowsAffected = sqlDB.update(UserProfileTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PURCHASE_HISTORY_ID:
                String purchaseHistoryId = uri.getLastPathSegment();
                StringBuilder purchaseHistorySelection = new StringBuilder(UserProfileTable._ID + "=" + purchaseHistoryId);
                if (!TextUtils.isEmpty(selection)) {
                    purchaseHistorySelection.append(" AND " + selection);
                }
                rowsAffected = sqlDB.update(UserProfileTable.TABLE_NAME, values, purchaseHistorySelection.toString(), null);
                break;
            case PURCHASE_HISTORY:
                rowsAffected = sqlDB.update(UserProfileTable.TABLE_NAME, values, selection, selectionArgs);
                break;


            default:
                throw new SQLException("Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (values.length == 0)
            return 0;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String table = getTable(uri);
            for (ContentValues value : values)
                db.insert(table, null, value);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return values.length;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final int numOperations = operations.size();
        if (numOperations == 0) {
            return new ContentProviderResult[0];
        }
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        mDb.beginTransaction();
        Set<Uri> uris = new HashSet<Uri>();
        try {
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                final ContentProviderOperation operation = operations.get(i);
                uris.add(operation.getUri());
                results[i] = operation.apply(this, results, i);
            }
            mDb.setTransactionSuccessful();
            return results;
        } finally {
            mDb.endTransaction();
            try {
                for (Uri u : uris)
                    getContext().getContentResolver().notifyChange(u, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private String getTable(Uri uri) {
        final int match = matcher.match(uri);
        String tableName = null;
        switch (match) {
            case USER_PROFILE:
                tableName = UserProfileTable.TABLE_NAME;
                break;
            case USER_AUTH:
                tableName = UserAuthTable.TABLE_NAME;
                break;
            case PURCHASE_HISTORY:
                tableName = PurchaseHistoryTable.TABLE_NAME;
                break;
        }
        if (tableName == null) {
            throw new SQLException("Invalid URI " + uri);
        }
        return tableName;
    }
}
