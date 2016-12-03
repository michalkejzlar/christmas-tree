package com.easycore.stromecek.views;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easycore.stromecek.R;
import com.easycore.stromecek.model.Venue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueDetailActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_SMS = 22321;
    public static String SMSRECEVID = "custom.action.SMSRECEVEDINFO";
    //toolbar
    @BindView(R.id.venuePictureImageView)
    protected ImageView venuePictureImageView;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.backdrop_toolbar)
    protected CollapsingToolbarLayout backdropToolbar;

    @BindView(R.id.venueNameTextView)
    protected TextView venueNameTextView;

    @BindView(R.id.txv_venue_dsc)
    protected TextView txvDsc;

    @BindView(R.id.btn_start_vide_stream)
    protected Button btnStartVideoStream;
    @BindView(R.id.btn_send_dms)
    protected Button btnSendDms;

    private ProgressDialog progressDialog;

    private Venue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);
        ButterKnife.bind(this);

        this.venue = getIntent().getParcelableExtra("venue");

        venueNameTextView.setText(venue.getName());
        btnSendDms.setText(getString(R.string.btn_send_dms, venue.getDmsCost()));
        txvDsc.setText(Html.fromHtml(venue.getDsc()));
        Glide.with(this)
                .load(venue.getPicture())
                .centerCrop()
                .into(venuePictureImageView);

        setupActionbar();

        btnSendDms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndSendSMS();
            }
        });
        btnStartVideoStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VenueDetailActivity.this, FullscreenPlayerActivity.class);
                intent.putExtra("venue", venue);
                startActivity(intent);
            }
        });

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

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    backdropToolbar.setTitle(venue.getName());
                    isShow = true;
                } else if (isShow) {
                    backdropToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void checkPermissionAndSendSMS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            sendSMS(venue.getDmsNumber(), venue.getDmsText());
            return;
        }

        final int status = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (PackageManager.PERMISSION_GRANTED == status) {
            sendSMS(venue.getDmsNumber(), venue.getDmsText());
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Snackbar.make(findViewById(android.R.id.content), R.string.sms_permissions_needed,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestSMSPermissions();
                            }
                        }).show();
            } else {
                requestSMSPermissions();
            }
        }
    }

    private void requestSMSPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS_SMS);
    }

    private void sendSMS(String phoneNumber, String message) {
        progressDialog = ProgressDialog.show(VenueDetailActivity.this, null,
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

}
