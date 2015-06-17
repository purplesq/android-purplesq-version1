package com.purplesq.purplesq.application;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.purplesq.purplesq.vos.EventsVo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 02/06/15.
 */
public class PurpleSQ extends Application {

    private List<EventsVo> eventsData;

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoaderConfiguration();
    }

    // Create global configuration and initialize ImageLoader with this configuration
    private void initImageLoaderConfiguration() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//        .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
//        .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//        .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .cacheOnDisk(true)
                .build();

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .diskCache(new LimitedAgeDiscCache(cacheDir, 691200)) // 8 days in seconds
                .defaultDisplayImageOptions(defaultOptions)
//                .writeDebugLogs()
                .diskCacheExtraOptions(800, 800, null)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public void setEventsData(List<EventsVo> events) {
        if (this.eventsData != null) {
            this.eventsData.clear();
        }
        this.eventsData = events;
    }

    public List<EventsVo> getEventsData() {
            return eventsData;
    }
}
