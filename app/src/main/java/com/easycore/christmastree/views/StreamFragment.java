package com.easycore.christmastree.views;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easycore.christmastree.BuildConfig;
import com.easycore.christmastree.R;
import com.easycore.christmastree.model.ChristmasColor;
import com.easycore.christmastree.model.Donation;
import com.easycore.christmastree.model.DonationsDb;
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
import io.codetail.animation.ViewAnimationUtils;

import java.util.List;
import java.util.Random;

import static com.easycore.christmastree.Config.TREE_STREAM_URL;
import static com.easycore.christmastree.utils.ViewUtils.getWindowHeightExcludedDecorViews;
import static com.easycore.christmastree.utils.ViewUtils.html;

public final class StreamFragment extends Fragment {

    private static final int BOTTOM_SHEET_VISIBLE_SCROLL_HEIGHT = 600;

    public static StreamFragment getInstance(ChristmasColor backgroundColor) {
        StreamFragment fr = new StreamFragment();
        Bundle args = new Bundle();
        args.putParcelable("backgroundColor", backgroundColor);
        fr.setArguments(args);
        return fr;
    }

    @BindView(R.id.scrollView)
    protected NestedScrollView scrollView;
    @BindView(R.id.videoLayout)
    protected FrameLayout videoLayout;
    @BindView(R.id.player_view)
    protected SimpleExoPlayerView playerView;
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
    private ChristmasColor christmasColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() == null || getArguments().getParcelable("backgroundColor") == null) {
                throw new IllegalArgumentException("You must start this fragment by it's starter methods.");
            }
            christmasColor = getArguments().getParcelable("backgroundColor");
        } else {
            christmasColor = savedInstanceState.getParcelable("backgroundColor");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stream_detail, container, false);
        ButterKnife.bind(this, view);

        // Set height to video frame
        videoLayout.getLayoutParams().height = getWindowHeightExcludedDecorViews(getActivity());

        setupBottomSheet((NestedScrollView) view.findViewById(R.id.bottom_sheet));
        bottomSheetLayout.setBackgroundColor(christmasColor.getMaterialColor());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupVideoPlayer();
        loadDonationProject();

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > BOTTOM_SHEET_VISIBLE_SCROLL_HEIGHT) {
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

//          start playback
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("backgroundColor", christmasColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().release();
    }

    private void setupBottomSheet(NestedScrollView bottomSheet) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void createCircularReveal(final View view) {
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
        view.setBackgroundColor(christmasColor.getMaterialColor());
        // Android native animator
        final Animator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(250);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                bottomSheetLayout.setBackgroundColor(christmasColor.getMaterialColor());
                animator.removeAllListeners();

            }
        });
        animator.start();
    }

    @OnClick(R.id.projectTextView)
    public void onProjectLabelClicked() {
        int scrollTo;
        if (scrollView.getScrollY() > 0) {
            // scroll back
            scrollTo = 0;
        } else {
            scrollTo = BOTTOM_SHEET_VISIBLE_SCROLL_HEIGHT + 100;
        }

        final ObjectAnimator animator = ObjectAnimator.ofInt(scrollView, "scrollY", scrollTo);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(250);
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

    @OnClick({R.id.redColorTxtView, R.id.greenColorTxtView,
            R.id.blueColorTxtView, R.id.yellowColorTxtView,
            R.id.purpleColorTxtView, R.id.orangeColorTxtView})
    public void onColorClicked(View button) {
        if (getView() == null) {
            return;
        }
        final View view = getView().findViewById(R.id.revealView);

        switch (button.getId()) {
            case R.id.redColorTxtView:
                christmasColor = ChristmasColor.red(getContext());
                break;
            case R.id.greenColorTxtView:
                christmasColor = ChristmasColor.green(getContext());
                break;
            case R.id.blueColorTxtView:
                christmasColor = ChristmasColor.blue(getContext());
                break;
            case R.id.yellowColorTxtView:
                christmasColor = ChristmasColor.yellow(getContext());
                break;
            case R.id.purpleColorTxtView:
                christmasColor = ChristmasColor.purple(getContext());
                break;
            case R.id.orangeColorTxtView:
                christmasColor = ChristmasColor.orange(getContext());
                break;
            default:
                throw new IllegalArgumentException("Undefined color clicked.");
        }
        createCircularReveal(view);
    }

    @OnClick(R.id.submitButton)
    void lightChristmasTree() {
        // TODO: 12/12/16 Send SMS
        ((MainActivity) getActivity()).lightChristmasTree(christmasColor);
        Toast.makeText(getActivity(), R.string.thank_you, Toast.LENGTH_SHORT).show();
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
        playerView.setUseController(false);
        playerView.setPlayer(player);

        final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), BuildConfig.APPLICATION_ID));
        player.setPlayWhenReady(true);
        HlsMediaSource hlsMediaSource = new HlsMediaSource(Uri.parse(TREE_STREAM_URL), dataSourceFactory, 0, null, null);
        player.prepare(hlsMediaSource);
    }
}
