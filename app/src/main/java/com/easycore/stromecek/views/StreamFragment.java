package com.easycore.stromecek.views;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import io.codetail.animation.ViewAnimationUtils;

import java.util.List;
import java.util.Random;

import static com.easycore.stromecek.utils.ViewUtils.html;
import static com.easycore.stromecek.views.SanitaryPlaceActivity.TEST_URL;

public final class StreamFragment extends Fragment {

    public static StreamFragment getInstance(int backgroundColor) {
        StreamFragment fr = new StreamFragment();
        Bundle args = new Bundle();
        args.putInt("backgroundColor", backgroundColor);
        fr.setArguments(args);
        return fr;
    }

    @BindView(R.id.scrollView)
    protected NestedScrollView scrollView;
    @BindView(R.id.videoLayout)
    protected FrameLayout videoLayout;
//    @BindView(R.id.player_view)
//    protected SimpleExoPlayerView playerView;
    @BindView(R.id.content_bottom_sheet)
    protected MyRevealFrameLayout bottomSheetLayout;

    @BindView(R.id.projectName)
    protected TextView projectNameTxtView;
    @BindView(R.id.projectDesc)
    protected TextView projectDescTxtView;
    @BindView(R.id.companyName)
    protected TextView companyNameTxtView;
    @BindView(R.id.companyDesc)
    protected TextView companyDescTxtView;
    @BindView(R.id.bottomSheetButton)
    protected TextView bottomSheetButton;

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || getArguments().getInt("backgroundColor", -1) == -1) {
            throw new IllegalArgumentException("You must start this fragment by it's starter methods.");
        }
    }

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

        setupBottomSheet((NestedScrollView) view.findViewById(R.id.bottom_sheet));
        bottomSheetLayout.setBackgroundColor(getArguments().getInt("backgroundColor"));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setupVideoPlayer();
        loadDonationProject();

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

    private void setupBottomSheet(NestedScrollView bottomSheet) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void createCircularReveal(final View view, final int color) {
        int cx;
        int cy;
        final Point lastTouch = bottomSheetLayout.getLastTouchPoint();

        if (lastTouch == null) {
            // get the center for the clipping circle
            cx = (view.getLeft() + view.getRight()) / 2;
            cy = (view.getTop() + view.getBottom()) / 2;
        } else {
            cx = lastTouch.x;
            cy = lastTouch.y;
        }

        // get the final radius for the clipping circle
        int dx = Math.max(cx, view.getWidth() - cx);
        int dy = Math.max(cy, view.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        view.setVisibility(View.VISIBLE);
        view.setBackgroundColor(color);
        // Android native animator
        final Animator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(250);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                if (getView() == null) {
                    return;
                }
                bottomSheetLayout.setBackgroundColor(color);
                animator.removeAllListeners();

            }
        });
        animator.start();
    }

    @OnClick(R.id.bottomSheetButton)
    public void slideUpBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @OnClick({R.id.redColorTxtView, R.id.greenColorTxtView, R.id.blueColorTxtView, R.id.yellowColorTxtView})
    public void onColorClicked(View button) {
        if (getView() == null) {
            return;
        }
        final View view = getView().findViewById(R.id.revealView);

        int colorResId;
        switch (button.getId()) {
            case R.id.redColorTxtView:
                colorResId = R.color.tree_material_red;
                break;
            case R.id.greenColorTxtView:
                colorResId = R.color.tree_material_green;
                break;
            case R.id.blueColorTxtView:
                colorResId = R.color.tree_material_blue;
                break;
            case R.id.yellowColorTxtView:
                colorResId = R.color.tree_material_yellow;
                break;
            default:
                throw new IllegalArgumentException("Undefined color clicked.");
        }

        @ColorInt int color = ContextCompat.getColor(getActivity(), colorResId);
        createCircularReveal(view, color);
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
