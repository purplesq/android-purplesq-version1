package com.purplesq.purplesq.datamanagers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.purplesq.purplesq.db.PsqContentProvider;
import com.purplesq.purplesq.db.PsqContract;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.UserVo;

/**
 * Created by nishant on 16/07/15.
 */
public class AuthDataManager {

    public static final int GET_AUTH_TOKEN = 0;
    public static final int GET_AUTH_USER_ID = 1;
    public static final int GET_AUTH_EMAIL = 2;
    public static final int GET_AUTH_FIRSTNAME = 3;
    public static final int GET_AUTH_LASTNAME = 4;
    public static final int GET_AUTH_PHONE = 5;
    public static final int GET_AUTH_INSTITUTE = 6;
    public static final int GET_AUTH_IMAGEURL = 7;
    public static final int GET_AUTH_DOB = 8;
    public static final int GET_AUTH_GENDER = 9;
    public static final int GET_AUTH_SOCIAL_LOGINS = 10;
    public static final int GET_AUTH_EXPIRY = 11;
    public static final int GET_AUTH_EXPIRY_TIME = 12;

    private static final String[] GET_AUTH_PROJECTION = new String[]{
            PsqContract.UserAuthTable.COL_TOKEN,
            PsqContract.UserAuthTable.COL_USER_ID,
            PsqContract.UserAuthTable.COL_EMAIL,
            PsqContract.UserAuthTable.COL_FIRSTNAME,
            PsqContract.UserAuthTable.COL_LASTNAME,
            PsqContract.UserAuthTable.COL_PHONE,
            PsqContract.UserAuthTable.COL_INSTITUTE,
            PsqContract.UserAuthTable.COL_IMAGEURL,
            PsqContract.UserAuthTable.COL_DOB,
            PsqContract.UserAuthTable.COL_GENDER,
            PsqContract.UserAuthTable.COL_SOCIAL_LOGINS,
            PsqContract.UserAuthTable.COL_EXPIRY,
            PsqContract.UserAuthTable.COL_EXPIRY_TIME
    };

    public static void insertOrUpdateAuthData(Context context, AuthVo authVo) {
        // Add data to public stream table
        ContentValues values = new ContentValues();

        values.put(PsqContract.UserAuthTable.COL_TOKEN, authVo.getToken());
        values.put(PsqContract.UserAuthTable.COL_USER_ID, authVo.getUser().getId());
        values.put(PsqContract.UserAuthTable.COL_EMAIL, authVo.getUser().getEmail());
        values.put(PsqContract.UserAuthTable.COL_FIRSTNAME, authVo.getUser().getFirstName());
        values.put(PsqContract.UserAuthTable.COL_LASTNAME, authVo.getUser().getLastName());
        values.put(PsqContract.UserAuthTable.COL_PHONE, authVo.getUser().getPhone());
        values.put(PsqContract.UserAuthTable.COL_INSTITUTE, authVo.getUser().getInstitute());
        values.put(PsqContract.UserAuthTable.COL_IMAGEURL, authVo.getUser().getImageurl());
        values.put(PsqContract.UserAuthTable.COL_DOB, authVo.getUser().getImageurl());
        values.put(PsqContract.UserAuthTable.COL_GENDER, authVo.getUser().getImageurl());
        values.put(PsqContract.UserAuthTable.COL_SOCIAL_LOGINS, authVo.getUser().getImageurl());
        values.put(PsqContract.UserAuthTable.COL_EXPIRY, authVo.getExpiry());
        values.put(PsqContract.UserAuthTable.COL_EXPIRY_TIME, authVo.getExpiryTime());

        ContentResolver resolver = context.getContentResolver();

        Cursor cur = null;
        try {
            cur = resolver.query(PsqContentProvider.URI_USER_AUTH, null, PsqContract.UserAuthTable.COL_USER_ID + " = ?", new String[]{authVo.getUser().getId()}, null);
            if ((cur != null) && !cur.isClosed() && cur.moveToFirst()) {
                values.remove(PsqContract.UserAuthTable.COL_USER_ID);
                resolver.update(PsqContentProvider.URI_USER_AUTH, values, PsqContract.UserAuthTable.COL_USER_ID + " = ?", new String[]{authVo.getUser().getId()});
            } else {
                resolver.insert(PsqContentProvider.URI_USER_AUTH, values);
            }
        } finally {
            if ((cur != null) && !cur.isClosed()) {
                cur.close();
            }
        }

        UserProfileDataManager.insertOrUpdateUserProfile(context, authVo.getUser());
    }

    public static AuthVo getAuthData(Context context) {
        Cursor cur = null;
        AuthVo authVo = new AuthVo();

        try {
            cur = context.getContentResolver().query(PsqContentProvider.URI_USER_AUTH, GET_AUTH_PROJECTION, null, null, null);
            if ((cur != null) && cur.moveToFirst()) {
                UserVo userVo = new UserVo();
                userVo.setId(cur.getString(GET_AUTH_USER_ID));
                userVo.setEmail(cur.getString(GET_AUTH_EMAIL));
                userVo.setFirstName(cur.getString(GET_AUTH_FIRSTNAME));
                userVo.setLastName(cur.getString(GET_AUTH_LASTNAME));
                userVo.setPhone(cur.getString(GET_AUTH_PHONE));
                userVo.setInstitute(cur.getString(GET_AUTH_INSTITUTE));
                userVo.setImageurl(cur.getString(GET_AUTH_IMAGEURL));
                userVo.setDob(cur.getLong(GET_AUTH_DOB));
                userVo.setGender(cur.getString(GET_AUTH_GENDER));
                authVo.setToken(cur.getString(GET_AUTH_TOKEN));
                authVo.setUser(userVo);
                authVo.setExpiry(cur.getLong(GET_AUTH_EXPIRY));
                authVo.setExpiryTime(cur.getLong(GET_AUTH_EXPIRY_TIME));

                return authVo;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        } finally {
            if ((cur != null) && !cur.isClosed()) {
                cur.close();
            }
        }

        return null;
    }
}
