package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.EventsVo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 02/06/15.
 */
public class GetAllEventsTask extends AsyncTask<Void, Void, String> {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private GenericAsyncTaskListener mListener;

    public GetAllEventsTask(GenericAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

//            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
//            qparams.add(new BasicNameValuePair("q", "httpclient"));
//            qparams.add(new BasicNameValuePair("btnG", "Google Search"));
//            qparams.add(new BasicNameValuePair("aq", "f"));
//            qparams.add(new BasicNameValuePair("oq", null));
//            URI uri = URIUtils.createURI("http", "www.google.com", -1, "/search",URLEncodedUtils.format(qparams, "UTF-8"), null);

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/eduventures/events/odis")
//                    .url("http://api.purplesq.com/eduventures/events/odis")
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                return null;
            }

            return response.body().string();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        List<EventsVo> eventsVos = null;

        @SuppressWarnings("serial")
        Type listType = new TypeToken<List<EventsVo>>() {
        }.getType();

        eventsVos = gson.fromJson(result, listType);

        if (mListener != null) {
            mListener.genericAsyncTaskOnSuccess(eventsVos);
        }
    }
}
