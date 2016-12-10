package com.easycore.stromecek.views;


import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easycore.stromecek.BuildConfig;
import com.easycore.stromecek.R;
import com.easycore.stromecek.model.Donation;
import com.easycore.stromecek.model.DonationsDb;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Random;

import static com.easycore.stromecek.utils.ViewUtils.html;
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
//    @BindView(R.id.player_view)
//    protected SimpleExoPlayerView playerView;
    @BindView(R.id.bottom_sheet)
    protected FrameLayout bottomSheet;

    @BindView(R.id.projectName)
    protected TextView projectNameTxtView;
    @BindView(R.id.projectDesc)
    protected TextView projectDescTxtView;
    @BindView(R.id.companyName)
    protected TextView companyNameTxtView;
    @BindView(R.id.companyDesc)
    protected TextView companyDescTxtView;
    @BindView(R.id.bottomSheetButton)
    protected Button bottomSheetButton;

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
        videoLayout.getLayoutParams().height = (int) (size.y * 0.85f);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setupVideoPlayer();

        setupBottomSheet();

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 500) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        loadDonationProject();

    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ShapeDrawable dr = new ShapeDrawable();
        dr.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }

    @OnClick(R.id.bottomSheetButton)
    public void slideUpBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // start playback
//        if (playerView.getPlayer() != null) {
//            playerView.getPlayer().setPlayWhenReady(true);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop playback
//        if (playerView.getPlayer() != null) {
//            playerView.getPlayer().setPlayWhenReady(false);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        playerView.getPlayer().release();
    }

    private void loadDonationProject() {
        final DonationsDb db = new DonationsDb(getActivity());
        final List<Donation> projects = db.getDonations();
        final Donation project = projects.get(new Random().nextInt(projects.size()));

        projectNameTxtView.setText(project.getProjectName());
        projectDescTxtView.setText(html(project.getProjectDescription()));
        companyNameTxtView.setText(project.getCompanyName());
        companyDescTxtView.setText(html(project.getCompanyDescription()));
    }

    private void setupVideoPlayer() {
        final Handler mainHandler = new Handler();
        final BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        final TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        final TrackSelector trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);
        final LoadControl loadControl = new DefaultLoadControl();
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

        // Bind the player to the view.
//        playerView.setUseController(false);
//        playerView.setPlayer(player);

        final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), BuildConfig.APPLICATION_ID));
        player.setPlayWhenReady(true);
        HlsMediaSource hlsMediaSource = new HlsMediaSource(Uri.parse(TEST_URL), dataSourceFactory, 0, null, null);
        player.prepare(hlsMediaSource);
    }
}
