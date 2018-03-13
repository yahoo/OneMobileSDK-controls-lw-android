/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.controls.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;

public class PlayerControlsVM {
    @NonNull
    public final LinkedList<TrackOptionVM> audioTracks = new LinkedList<>();
    @NonNull
    public final LinkedList<TrackOptionVM> ccTracks = new LinkedList<>();
    public boolean isLoading;
    public boolean isPlayButtonVisible;
    public boolean isPauseButtonVisible;
    public boolean isReplayButtonVisible;
    public boolean isNextButtonVisible;
    public boolean isNextButtonEnabled;
    public boolean isPrevButtonVisible;
    public boolean isPrevButtonEnabled;
    public boolean isSeekerVisible;
    public boolean isSeekForwardButtonVisible;
    public boolean isSeekBackButtonVisible;
    public boolean isTitleVisible;
    public boolean isSubtitlesTextVisible;
    public boolean isCompassViewVisible;
    public boolean isThumbnailImageVisible;
    public boolean isStreamPlaying;
    public boolean isTrackChooserButtonEnabled;
    public boolean isTrackChooserButtonVisible;
    public int seekerBufferedProgress;
    public int seekerMaxValue = 1;
    public double seekerProgress;
    public double compassLatitude;
    public double compassLongitude;
    @Nullable
    public String seekerCurrentTimeText;
    @Nullable
    public String seekerTimeLeftText;
    @Nullable
    public String seekerDurationText;
    @Nullable
    public String titleText;
    @Nullable
    public CharSequence subtitlesText;
    @Nullable
    public String thumbnailImageUrl;
    public boolean isLiveIndicatorVisible;
    public boolean isOnLiveEdge;
    public boolean isCastButtonVisible;
}
