package com.easycore.stromecek.views;


import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easycore.stromecek.BuildConfig;
import com.easycore.stromecek.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.easycore.stromecek.views.SanitaryPlaceActivity.TEST_URL;

public final class StreamFragment extends Fragment {

    public static StreamFragment getInstance(int position) {
        StreamFragment fr = new StreamFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fr.setArguments(args);
        return fr;
    }

    @BindView(R.id.scrollView)
    protected NestedScrollView scrollView;
    @BindView(R.id.videoLayout)
    protected FrameLayout videoLayout;
    @BindView(R.id.player_view)
    protected SimpleExoPlayerView playerView;

    @BindView(R.id.bottom_sheet)
    protected FrameLayout bottomSheet;

    private BottomSheetBehavior bottomSheetBehavior;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream_detail, container, false);
        ButterKnife.bind(this, view);

        // set video view to 80% of display height.
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        videoLayout.getLayoutParams().height = (int) (size.y * 0.8f);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupHLS();

        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                final int previousState = bottomSheetBehavior.getState();
                if (scrollY > 500) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // start playback
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop playback
        if (playerView.getPlayer() != null) {
            playerView.getPlayer().setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().release();
    }

    void setupHLS() {
        // 1. Create a default TrackSelector
        final Handler mainHandler = new Handler();
        final BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        final TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        final TrackSelector trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        final LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

        // Bind the player to the view.
        playerView.setUseController(false);
        playerView.setPlayer(player);

        final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), BuildConfig.APPLICATION_ID));
        player.setPlayWhenReady(true);

        HlsMediaSource hlsMediaSource = new HlsMediaSource(
                Uri.parse(TEST_URL),
                dataSourceFactory,
                0, null, null
        );
        player.prepare(hlsMediaSource);
    }
}
