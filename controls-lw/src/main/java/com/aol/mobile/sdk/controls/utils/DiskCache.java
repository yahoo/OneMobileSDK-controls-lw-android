/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@WorkerThread
class DiskCache {
    @Nullable
    BitmapDrawable getDrawable(@NonNull Context context, @NonNull String resName,
                               boolean isForTablet) {
        synchronized (this) {
            File drawableFile = new File(context.getCacheDir(), resName + "_" + isForTablet);

            if (!drawableFile.exists() || !drawableFile.isFile()) return null;

            return new BitmapDrawable(context.getResources(), drawableFile.getAbsolutePath());
        }
    }

    synchronized void putDrawable(@NonNull Context context, @NonNull String resName,
                                  boolean isForTablet, @NonNull BitmapDrawable drawable) {
        synchronized (this) {
            File drawableFile = new File(context.getCacheDir(), resName + "_" + isForTablet);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(drawableFile);
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }
}
