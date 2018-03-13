/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.viewmodel;

import android.support.annotation.Nullable;

public class AdControlsVM {
    public boolean isProgressViewVisible;
    public boolean isPlayButtonVisible;
    public boolean isPauseButtonVisible;
    public boolean isAdTimeViewVisible;
    public double seekerProgress;
    public int seekerMaxValue;
    @Nullable
    public String currentTimeText;
    @Nullable
    public String timeLeftText;
    @Nullable
    public String durationText;
}
