/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.viewmodel;

import android.support.annotation.Nullable;

public class TrackOptionVM {
    @Nullable
    public String title;
    public boolean isSelected;

    public TrackOptionVM(@Nullable String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }
}
