/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.aol.mobile.sdk.controls.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;
import static com.aol.mobile.sdk.controls.BuildConfig.REMOTE_CONTROLS_HOST;

public class ImageUtils {
    private static final DiskCache diskCache = new DiskCache();

    @UiThread
    public static void warmUpCache(@NonNull Context context) {
        warmUpCache(context, new FillCallback() {
            @Override
            public void onCacheFilled(long timeElapsedMs, boolean hasMissingEntries) {
                Log.d("LWCAHE", "Warm up time: " + timeElapsedMs + "ms, hasMissing: " + hasMissingEntries);
            }
        });
    }

    @UiThread
    public static void warmUpCache(@NonNull final Context context, @NonNull final FillCallback fillCallback) {
        final Handler handler = new Handler();
        final boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        final long startTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] remoteResources = new String[]{
                        "ic_ad_pause",
                        "ic_ad_play",
                        "ic_ad_seekbar_full",
                        "ic_ad_seekbar_played",
                        "ic_back",
                        "ic_forward",
                        "ic_back_on",
                        "ic_10sec",
                        "ic_pause",
                        "ic_forward_on",
                        "ic_play",
                        "ic_thumbseek_background",
                        "ic_tracks",
                        "ic_10sec_on",
                        "ic_ad_loading",
                        "ic_10sec",
                        "ic_thumb",
                        "ic_tracks_on",
                        "ic_loading",
                        "ic_halo",
                        "ic_10sec_on",
                        "ic_halo",
                        "ic_thumbseek",
                        "ic_replay",
                        "ic_halo",
                        "ic_360_compass_body",
                        "ic_360_compass_direction",
                        "ic_tracks_close",
                        "ic_track_selected"
                };

                int missEntriesCount = 0;

                for (String remoteSource : remoteResources) {
                    BitmapDrawable fromDisk = getFromDisk(context, remoteSource, isTablet);
                    if (fromDisk != null) continue;

                    BitmapDrawable drawable = getFromRemote(context, remoteSource, isTablet);

                    if (drawable == null) {
                        missEntriesCount++;
                    } else {
                        diskCache.putDrawable(context, remoteSource, isTablet, drawable);
                    }
                }

                final boolean hasMissingEntries = missEntriesCount > 0;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillCallback.onCacheFilled(System.currentTimeMillis() - startTime, hasMissingEntries);
                    }
                });
            }
        }).start();
    }

    @UiThread
    public static void loadRemoteResAsDrawable(@NonNull final Context context,
                                               @NonNull final String remoteSource,
                                               @NonNull final LoadCallback callback) {

        final Handler handler = new Handler();
        final boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapDrawable drawable = getFromDisk(context, remoteSource, isTablet);

                if (drawable == null) {
                    drawable = getFromRemote(context, remoteSource, isTablet);
                }

                if (drawable == null) {
                    Log.w("LWCAHE", "Failed to load resource:" + remoteSource + (isTablet ? " for tablet" : " for phone"));
                    return;
                }

                final BitmapDrawable finalDrawable = drawable;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDrawableLoaded(finalDrawable);
                    }
                });

                diskCache.putDrawable(context, remoteSource, isTablet, drawable);
            }
        }).start();

    }

    @Nullable
    @WorkerThread
    private static BitmapDrawable getFromRemote(@NonNull Context context, @NonNull String remoteSource, boolean isTablet) {
        final Resources resources = context.getResources();
        final String tabletUrl = REMOTE_CONTROLS_HOST + "drawable-sw600dp-xxxhdpi/" + remoteSource + ".png";
        final String phoneUrl = REMOTE_CONTROLS_HOST + "drawable-xxxhdpi/" + remoteSource + ".png";

        BitmapDrawable drawable = drawableFromUrl(resources, isTablet ? tabletUrl : phoneUrl);

        if (drawable == null && isTablet) {
            drawable = drawableFromUrl(resources, phoneUrl);
        }

        return drawable;
    }

    @Nullable
    @WorkerThread
    private static BitmapDrawable getFromDisk(@NonNull Context context, @NonNull String remoteSource, boolean isTablet) {
        BitmapDrawable drawable = diskCache.getDrawable(context, remoteSource, isTablet);

        if (drawable == null && isTablet)
            return diskCache.getDrawable(context, remoteSource, false);
        else
            return drawable;
    }

    @Nullable
    @WorkerThread
    private static BitmapDrawable drawableFromUrl(@NonNull Resources resources, @NonNull String url) {
        InputStream input;

        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.connect();
            input = connection.getInputStream();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, input);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);

        Log.d("LWCAHE", "Downloaded: " + url);

        return bitmapDrawable;
    }

    @UiThread
    public static void loadSeekerDrawable(@NonNull final Context context,
                                          @NonNull final String seekerBg,
                                          @NonNull final String seekerProgress,
                                          @NonNull final LoadCallback loadCallback) {

        loadRemoteResAsDrawable(context, seekerBg, new LoadCallback() {
            @Override
            public void onDrawableLoaded(@NonNull final Drawable bgDrawable) {
                loadRemoteResAsDrawable(context, seekerProgress, new LoadCallback() {
                    @Override
                    public void onDrawableLoaded(@NonNull Drawable progressDrawable) {
                        @SuppressLint("RtlHardcoded")
                        LayerDrawable drawable = new LayerDrawable(new Drawable[]{
                                bgDrawable,
                                new ClipDrawable(progressDrawable, CENTER_VERTICAL | LEFT, HORIZONTAL),
                                new ClipDrawable(progressDrawable, CENTER_VERTICAL | LEFT, HORIZONTAL)
                        });
                        drawable.setId(0, android.R.id.background);
                        drawable.setId(1, android.R.id.secondaryProgress);
                        drawable.setId(2, android.R.id.progress);
                        loadCallback.onDrawableLoaded(drawable);
                    }
                });
            }
        });
    }

    public interface LoadCallback {
        void onDrawableLoaded(@NonNull Drawable drawable);
    }

    public interface FillCallback {
        void onCacheFilled(long timeElapsedMs, boolean hasMissingEntries);
    }
}
