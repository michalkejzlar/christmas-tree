package com.easycore.stromecek.views;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easycore.stromecek.BuildConfig;
import com.easycore.stromecek.R;
import com.easycore.stromecek.model.Donation;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.*;
import com.google.android.exoplayer2.util.Util;

public class SanitaryPlaceActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_SMS = 22321;
    public static final String TEST_URL = "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear1/prog_index.m3u8";
    public static String SMSRECEVID = "custom.action.SMSRECEVEDINFO";

    //toolbar
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

//    @BindView(R.id.player_view)
//    protected SimpleExoPlayerView playerView;

    private ProgressDialog progressDialog;

    private Donation place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stream_detail);
        ButterKnife.bind(this);

        this.place = getIntent().getParcelableExtra("place");

//        venueNameTextView.setText(place.getName());
//        btnSendDms.setText(getString(R.string.btn_send_dms, place.getDmsCost()));
//        txvDsc.setText(Html.fromHtml(place.getDsc()));

        setupActionbar();
//        btnSendDms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkPermissionAndSendSMS();
//            }
//        });
    }

    private void setupActionbar() {
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (playerView.getPlayer() == null) {
//            setupHLS();
//        } else {
//            playerView.getPlayer().setPlayWhenReady(true);
//        }


        final IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                try {
                    if (bundle != null) {
                        Object[] objects = (Object[]) bundle.get("pdus");
                        for (Object obj : objects) {
                            SmsMessage currentMessage = SmsMessage
                                    .createFromPdu((byte[]) obj);
                            String phoneNumber = currentMessage
                                    .getDisplayOriginatingAddress();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        playerView.getPlayer().setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        playerView.getPlayer().release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void checkPermissionAndSendSMS() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            sendSMS(place.getDmsNumber(), place.getDmsText());
//            return;
//        }
//
//        final int status = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
//
//        if (PackageManager.PERMISSION_GRANTED == status) {
//            sendSMS(place.getDmsNumber(), place.getDmsText());
//        } else {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
//                Snackbar.make(findViewById(android.R.id.content), R.string.sms_permissions_needed,
//                        Snackbar.LENGTH_INDEFINITE)
//                        .setAction(android.R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                requestSMSPermissions();
//                            }
//                        }).show();
//            } else {
//                requestSMSPermissions();
//            }
//        }
//    }
//
//    private void requestSMSPermissions() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS_SMS);
//    }

    private void sendSMS(String phoneNumber, String message) {
        progressDialog = ProgressDialog.show(SanitaryPlaceActivity.this, null,
                "Pos9l8m", true, false);
        progressDialog.show();

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    private void showUserMessagePickerDialog(){
        DialogFragment dialogFragment = new DialogFragment();

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
        final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        // Bind the player to the view.
//        playerView.setUseController(false);
//        playerView.setPlayer(player);

        final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,
                BuildConfig.APPLICATION_ID));
        player.setPlayWhenReady(true);

        HlsMediaSource hlsMediaSource = new HlsMediaSource(
                Uri.parse(TEST_URL),
                dataSourceFactory,
                0, null, null
        );
        player.prepare(hlsMediaSource);
    }


    void setupMPEG() {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        // Bind the player to the view.
//        playerView.setPlayer(player);

        player.setPlayWhenReady(true);


        HttpDataSource.Factory mDataSourceFactory;
        mDataSourceFactory = new DefaultHttpDataSourceFactory("DrmPlayActivity");

        DashMediaSource dashMediaSource = new DashMediaSource(Uri.parse("http://52.210.200.55:1935/stromcek/myStream/manifest.mpd"),
                mDataSourceFactory,
                new DefaultDashChunkSource.Factory(mDataSourceFactory),
                null,
                null
        );
        player.prepare(dashMediaSource, true, true);

    }

}
