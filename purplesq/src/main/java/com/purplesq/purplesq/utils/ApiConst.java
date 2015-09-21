package com.purplesq.purplesq.utils;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by nishant on 14/08/15.
 */
public class ApiConst {

    private static OkHttpClient okHttpClient;
    private static Gson gson;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static final String HEADER_PARAM_ANDROID = "android";
    public static final String HEADER_PARAM_JSON = "application/json";
    public static final String HEADER_PLATFORM = "platform";
    public static final String HEADER_PURPLE_TOKEN = "Purple-Token";
    public static final String HEADER_ACCESS_TOKEN = "access-token";
    public static final String HEADER_CONTENT_TYPE = "content-type";


    public static final String URL_GET_ALL_CITIES = Config.getPurplesqBaseUrl() + "eduventures/events/cities";
    public static final String URL_GET_ALL_EVENTS = Config.getPurplesqBaseUrl() + "eduventures/events/odis";
    public static final String URL_GET_USER_PROFILE = Config.getPurplesqBaseUrl() + "users/account";
    public static final String URL_PROFILE_UPDATE = Config.getPurplesqBaseUrl() + "users/account";
    public static final String URL_LOGIN_REGISTER = Config.getPurplesqBaseUrl() + "users/account";
    public static final String URL_LOGIN_EMAIL = Config.getPurplesqBaseUrl() + "users/login";
    public static final String URL_LOGIN_FACEBOOK = Config.getPurplesqBaseUrl() + "users/facebook";
    public static final String URL_LOGIN_GOOGLE = Config.getPurplesqBaseUrl() + "users/google";
    public static final String URL_PAYMENT = Config.getPurplesqBaseUrl() + "payments/process/";
    public static final String URL_REFRESH_TOKEN = Config.getPurplesqBaseUrl() + "users/refresh-token";
    public static final String URL_PAYMENT_INITIATE = Config.getPurplesqBaseUrl() + "payments/events/";
    public static final String URL_PAYMENT_COD_PINCODE_CHECK = Config.getPurplesqBaseUrl() + "payments/events/check-pincode";
    public static final String URL_PAYMENT_INITIATE_PART = "/initiate";
    public static final String URL_INVOICES = Config.getPurplesqBaseUrl() + "users/purchaseHistory";
    public static final String URL_CHECK_COUPON = Config.getPurplesqBaseUrl() + "components/check-coupon";


    public static OkHttpClient getHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout((60 * 1000), TimeUnit.MILLISECONDS);
        }

        return okHttpClient;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

}
