package com.purplesq.purplesq.datamanagers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.purplesq.purplesq.db.PsqContentProvider;
import com.purplesq.purplesq.db.PsqContract.UserProfileTable;
import com.purplesq.purplesq.vos.UserVo;
import com.purplesq.purplesq.vos.UserVo.UserRolesVo;
import com.purplesq.purplesq.vos.UserVo.UserStatusVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 15/07/15.
 */
public class UserProfileDataManager {

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
            UserProfileTable.COL_SOCIAL_LOGINS,
            UserProfileTable.COL_ROLE,
            UserProfileTable.COL_STATUS_ACCOUNT,
            UserProfileTable.COL_STATUS_EMAIL,
            UserProfileTable.COL_STATUS_PHONE
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
    public static final int GET_ROLE = 10;
    public static final int GET_STATUS_ACCOUNT = 11;
    public static final int GET_STATUS_EMAIL = 12;
    public static final int GET_STATUS_PHONE = 13;

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
//        values.put(UserProfileTable.COL_SOCIAL_LOGINS, userVo.getSocial_logins());
        values.put(UserProfileTable.COL_ROLE, userVo.getRoles().get(0).getRole());
        values.put(UserProfileTable.COL_STATUS_ACCOUNT, userVo.getStatus().getAccount());
        values.put(UserProfileTable.COL_STATUS_EMAIL, userVo.getStatus().getEmail());
        values.put(UserProfileTable.COL_STATUS_PHONE, userVo.getStatus().getPhone());

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
        Cursor cur;
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
                userVo.setGender(cur.getString(GET_SOCIAL_LOGINS));

                List<UserRolesVo> roles = new ArrayList<>();
                userVo.setRoles(roles);
                userVo.getRoles().add(userVo.new UserRolesVo("", cur.getString(GET_ROLE)));
                UserStatusVo userStatusVo = userVo.new UserStatusVo(cur.getString(GET_STATUS_ACCOUNT), cur.getString(GET_STATUS_PHONE), cur.getString(GET_STATUS_EMAIL));
                userVo.setStatus(userStatusVo);

                return userVo;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static UserVo getUserBacisProfile(Context context) {
        Cursor cur;
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
            return null;
        }

        return null;
    }
}
