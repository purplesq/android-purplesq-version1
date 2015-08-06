package com.purplesq.purplesq.db;

import android.provider.BaseColumns;

/**
 * Created by nishant on 13/07/15.
 */
public final class PsqContract {

    public PsqContract() {
    }

    public static abstract class UserProfileTable implements BaseColumns {
        public static final String TABLE_NAME = "userProfileTable";
        public static final String COL_USER_ID = "userId";
        public static final String COL_EMAIL = "email";
        public static final String COL_FIRSTNAME = "firstName";
        public static final String COL_LASTNAME = "lastName";
        public static final String COL_PHONE = "phone";
        public static final String COL_DOB = "dob";
        public static final String COL_GENDER = "gender";
        public static final String COL_INSTITUTE = "institute";
        public static final String COL_IMAGEURL = "imageUrl";
        public static final String COL_SOCIAL_LOGINS = "social_logins";

//        "user":{"_id":"5589688ce372cf3202cbe71f", "fname":"Nishant", "lname":"Patil", "email":"nishant@purplesq.com", "dob":null, "qualifications":[],"phone":"9975851484", "roles":[{"_id":"553e0a9599cfc9064cf1d32e", "role":"normal"}],"status":{"account":"Enabled", "phone":"Unverified", "email":"Unverified"}}

    }

    public static abstract class UserAuthTable implements BaseColumns {
        public static final String TABLE_NAME = "userAuthTable";
        public static final String COL_TOKEN = "token";
        public static final String COL_USER_ID = "userId";
        public static final String COL_EMAIL = "email";
        public static final String COL_FIRSTNAME = "firstName";
        public static final String COL_LASTNAME = "lastName";
        public static final String COL_PHONE = "phone";
        public static final String COL_DOB = "dob";
        public static final String COL_GENDER = "gender";
        public static final String COL_INSTITUTE = "institute";
        public static final String COL_IMAGEURL = "imageUrl";
        public static final String COL_SOCIAL_LOGINS = "social_logins";
        public static final String COL_EXPIRY = "expiry";
        public static final String COL_EXPIRY_TIME = "expiryTime";

    }

    public static abstract class PurchaseHistoryTable implements BaseColumns {
        public static final String TABLE_NAME = "purchaseHistoryTable";
        public static final String COL_TRANS_ID = "transactionId";
        public static final String COL_PURCHASE_JSON = "purchaseJson";
    }


}
