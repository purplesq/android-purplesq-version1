package com.purplesq.purplesq.utils;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by nishant on 01/09/15.
 */
public class OkHttpLoggingInterceptor implements Interceptor {
    private final String TAG = "HTTP";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.i(TAG, "**************************************");
        Log.i(TAG, "**************************************");
        Log.i(TAG, "Sending Request");
        String method = request.method();

        Log.i(TAG, "Request Method : " + method);
        if (method.equals("GET")) {
            Log.i(TAG, "Request Url : " + request.url());
            Log.i(TAG, "Headers : " + request.headers());
        } else if (method.equals("POST")) {
            Log.i(TAG, "Request : " + request.url());
            Log.i(TAG, "Headers : " + request.headers());
            Log.i(TAG, "Body : " + Utils.bodyToString(request));
        } else if (method.equals("PUT")) {
            Log.i(TAG, "Request : " + request.url());
            Log.i(TAG, "Headers : " + request.headers());
            Log.i(TAG, "Body : " + Utils.bodyToString(request));
        }

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        double time = (t2 - t1) / 1e6d;

        Log.i(TAG, "\n Received Response");
        Log.i(TAG, "Response Code : " + response.code());
        Log.i(TAG, "Response Message : " + response.message());
        Log.i(TAG, "Time taken : " + time + " ms");

        return response;
    }
}