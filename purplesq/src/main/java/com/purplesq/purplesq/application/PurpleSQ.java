package com.purplesq.purplesq.application;

import android.app.Application;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.purplesq.purplesq.fragments.LoadingDialogFragment;
import com.purplesq.purplesq.vos.EventsVo;

import java.io.File;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by nishant on 02/06/15.
 */
public class PurpleSQ extends Application {

    private static DialogFragment mLoadingDialogFragment;
    private static boolean isShowDialogCalled = false;
    private List<EventsVo> eventsData;

    public static void showLoadingDialog(FragmentActivity activity) {
        if (isShowDialogCalled) {
            return;
        }

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        // Create and show the dialog.
        if (mLoadingDialogFragment == null) {
            mLoadingDialogFragment = LoadingDialogFragment.newInstance();
        }

        isShowDialogCalled = true;
        mLoadingDialogFragment.show(ft, "dialog");
    }

    public static void dismissLoadingDialog() {
        if (mLoadingDialogFragment != null) {
            if (mLoadingDialogFragment.isVisible()) {
                mLoadingDialogFragment.dismiss();
                isShowDialogCalled = false;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

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

    public List<EventsVo> getEventsData() {
        return eventsData;
    }

    public void setEventsData(List<EventsVo> events) {
        if (this.eventsData != null) {
            this.eventsData.clear();
        }
        this.eventsData = events;
    }
}
