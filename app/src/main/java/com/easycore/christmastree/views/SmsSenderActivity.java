package com.easycore.christmastree.views;


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
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;
import com.easycore.christmastree.Config;
import com.easycore.christmastree.R;
import com.easycore.christmastree.model.Donation;

abstract class SmsSenderActivity extends AppCompatActivity {

    protected static final int REQUEST_PERMISSIONS_SMS = 0x00300;
    private final static String SENT = "com.easycore.stromecek.SMS_SENT";
    private final static String DELIVERED = "com.easycore.stromecek.SMS_DELIVERED";

    private final static String PERMISSION = "android.permission.RECEIVE_SMS";

    abstract void onSMSSent();

    private final SmsSentReceiver smsSentReceiver = new SmsSentReceiver();
    private final SmsDeliveredReceiver smsDeliveredReceiver = new SmsDeliveredReceiver();

    private ProgressDialog progressDialog = null;
    private final Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsSentReceiver, new IntentFilter(SENT), PERMISSION, handler);
//        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
//        unregisterReceiver(smsDeliveredReceiver);
    }

    protected void checkPermissionAndSendSMS(Donation donation) {

        // If SDK lower than Lollipop, send SMS right away
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            sendSMS(donation);
            return;
        }

        // check permissions
        final int statusSend = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        final int statusReceive = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if (PackageManager.PERMISSION_GRANTED == statusSend
                && PackageManager.PERMISSION_GRANTED == statusReceive) {
            sendSMS(donation);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Snackbar.make(findViewById(android.R.id.content), R.string.sms_permissions_needed,
                        Snackbar.LENGTH_LONG)
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
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS},
                REQUEST_PERMISSIONS_SMS);
    }

    private void sendSMS(final Donation donation) {
        this.progressDialog = ProgressDialog
                .show(SmsSenderActivity.this, null, getString(R.string.sending_sms), true, false);

        final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);
        final PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), PendingIntent.FLAG_CANCEL_CURRENT);

        final SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Config.DONATION_PHONE_NUMBER, null,
                Config.dmsCode(donation.getSmsCode()), sentPI, null);
    }

    private void showFailureDialog() {
        final String sadFace = new String(Character.toChars(0x1F622)); // :-(
        final String text = getString(R.string.sms_fail, sadFace);
        Snackbar.make(findViewById(android.R.id.content), text,
                Snackbar.LENGTH_LONG)
                .show();
    }

    final class SmsSentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    onSMSSent();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    showFailureDialog();
                    break;
            }
        }
    }

    final class SmsDeliveredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
