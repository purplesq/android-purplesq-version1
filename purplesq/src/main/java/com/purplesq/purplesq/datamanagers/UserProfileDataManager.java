package com.purplesq.purplesq.datamanagers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.db.PsqContentProvider;
import com.purplesq.purplesq.db.PsqContract.UserProfileTable;
import com.purplesq.purplesq.vos.UserVo;

/**
 * Created by nishant on 15/07/15.
 */
public class UserProfileDataManager {

    public static final int GET_USER_ID = 0;
    public static final int GET_EMAIL = 1;
    public static final int GET_FIRSTNAME = 2;
    public static final int GET_LASTNAME = 3;
    public static final int GET_PHONE = 4;
    public static final int GET_INSTITUTE = 5;
    public static final int GET_IMAGEURL = 6;
    public static final int GET_DOB = 7;
    public static final int GET_GENDER = 8;
    public static final int GET_SOCIAL_LOGINS = 9;

    private static final String[] GET_USER_PROFILE_PROJECTION = new String[]{
            UserProfileTable.COL_USER_ID,
            UserProfileTable.COL_EMAIL,
            UserProfileTable.COL_FIRSTNAME,
            UserProfileTable.COL_LASTNAME,
            UserProfileTable.COL_PHONE,
            UserProfileTable.COL_INSTITUTE,
            UserProfileTable.COL_IMAGEURL,
            UserProfileTable.COL_DOB,
            UserProfileTable.COL_GENDER,
            UserProfileTable.COL_SOCIAL_LOGINS
    };
    private static final String[] GET_USER_BASIC_PROFILE_PROJECTION = new String[]{
            UserProfileTable.COL_USER_ID,
            UserProfileTable.COL_EMAIL,
            UserProfileTable.COL_FIRSTNAME,
            UserProfileTable.COL_LASTNAME,
            UserProfileTable.COL_PHONE,
            UserProfileTable.COL_INSTITUTE,
            UserProfileTable.COL_IMAGEURL,
    };

    public static void insertOrUpdateUserProfile(Context context, UserVo userVo) {
        // Add data to public stream table
        ContentValues values = new ContentValues();

        values.put(UserProfileTable.COL_USER_ID, userVo.getId());
        values.put(UserProfileTable.COL_EMAIL, userVo.getEmail());
        values.put(UserProfileTable.COL_FIRSTNAME, userVo.getFirstName());
        values.put(UserProfileTable.COL_LASTNAME, userVo.getLastName());
        values.put(UserProfileTable.COL_PHONE, userVo.getPhone());
        values.put(UserProfileTable.COL_INSTITUTE, userVo.getInstitute());
        values.put(UserProfileTable.COL_IMAGEURL, userVo.getImageurl());
        values.put(UserProfileTable.COL_DOB, userVo.getDob());
        values.put(UserProfileTable.COL_GENDER, userVo.getGender());

        ContentResolver resolver = context.getContentResolver();

        Cursor cur = null;
        try {
            cur = resolver.query(PsqContentProvider.URI_USER_PROFILE, null, UserProfileTable.COL_USER_ID + " = ?", new String[]{userVo.getId()}, null);
            if ((cur != null) && !cur.isClosed() && cur.moveToFirst()) {
                values.remove(UserProfileTable.COL_USER_ID);
                resolver.update(PsqContentProvider.URI_USER_PROFILE, values, UserProfileTable.COL_USER_ID + " = ?", new String[]{userVo.getId()});
            } else {
                resolver.insert(PsqContentProvider.URI_USER_PROFILE, values);
            }
        } finally {
            if ((cur != null) && !cur.isClosed()) {
                cur.close();
            }
        }
    }

    public static UserVo getUserProfile(Context context) {
        Cursor cur = null;
        UserVo userVo = new UserVo();
        try {
            cur = context.getContentResolver().query(PsqContentProvider.URI_USER_PROFILE, GET_USER_PROFILE_PROJECTION, null, null, null);
            if ((cur != null) && cur.moveToFirst()) {
                userVo.setId(cur.getString(GET_USER_ID));
                userVo.setEmail(cur.getString(GET_EMAIL));
                userVo.setFirstName(cur.getString(GET_FIRSTNAME));
                userVo.setLastName(cur.getString(GET_LASTNAME));
                userVo.setPhone(cur.getString(GET_PHONE));
                userVo.setInstitute(cur.getString(GET_INSTITUTE));
                userVo.setImageurl(cur.getString(GET_IMAGEURL));
                userVo.setDob(cur.getLong(GET_DOB));
                userVo.setGender(cur.getString(GET_GENDER));

                return userVo;
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

    public static UserVo getUserBacisProfile(Context context) {
        Cursor cur = null;
        UserVo userVo = new UserVo();
        try {
            cur = context.getContentResolver().query(PsqContentProvider.URI_USER_PROFILE, GET_USER_BASIC_PROFILE_PROJECTION, null, null, null);
            if ((cur != null) && cur.moveToFirst()) {
                userVo.setId(cur.getString(GET_USER_ID));
                userVo.setEmail(cur.getString(GET_EMAIL));
                userVo.setFirstName(cur.getString(GET_FIRSTNAME));
                userVo.setLastName(cur.getString(GET_LASTNAME));
                userVo.setPhone(cur.getString(GET_PHONE));
                userVo.setInstitute(cur.getString(GET_INSTITUTE));
                userVo.setImageurl(cur.getString(GET_IMAGEURL));
                return userVo;
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
