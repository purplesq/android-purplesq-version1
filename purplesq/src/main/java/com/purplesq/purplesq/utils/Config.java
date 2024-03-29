package com.purplesq.purplesq.utils;

import android.content.Context;
import android.text.TextUtils;

import com.purplesq.purplesq.R;

/**
 * Created by nishant on 12/08/15.
 */
public class Config {

    public static final String ENV_QA = "qa";

    public static final String ENV_PROD = "prod";

    public static final String ENV_DEFAULT = ENV_PROD;

    public static boolean DEBUG = false;

    private static String purplesqBaseUrl = null;

    private static boolean isProd = true;


    public static void configureEnvironment(Context context) {
        String env = context.getString(R.string.env);
        if (env == null) {
            env = Config.ENV_DEFAULT;
        }

        env = env.toLowerCase();

        if (env.equals(Config.ENV_PROD)) {
            purplesqBaseUrl = context.getString(R.string.url_purplesq_prod);
            isProd = true;
        } else if (env.equals(Config.ENV_QA)) {
            purplesqBaseUrl = context.getString(R.string.url_purplesq_qa);
            isProd = false;
        } else {
            purplesqBaseUrl = context.getString(R.string.url_purplesq_prod);
            isProd = true;
        }
        
    }

    public static String getPurplesqBaseUrl() {
        return purplesqBaseUrl;
    }

    public static boolean isProd() {
        return isProd;
    }

}
