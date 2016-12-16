package com.easycore.christmastree.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easycore.christmastree.BuildConfig;
import com.easycore.christmastree.R;
import com.easycore.christmastree.model.ChristmasColor;
import com.easycore.christmastree.model.Donation;
import com.easycore.christmastree.model.LightRequest;
import com.easycore.christmastree.utils.FirebaseDatabaseEventAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends SmsSenderActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.viewPager)
    protected ViewPager viewPager;
//    @BindView(R.id.titles)
//    protected ViewPagerIndicator pagerIndicator;

    private ChristmasColor selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), ChristmasColor.random(this)));

        if (savedInstanceState != null) {
            selectedColor = savedInstanceState.getParcelable("selectedColor");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("selectedColor", selectedColor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void sendSMSAndLightChristmasTree(final Donation donation, final ChristmasColor color) {

        if (BuildConfig.DEBUG) {
            lightChristmasTree(color);
            return;
        }

        // confirm it first
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sms_confirmation_title);
        builder.setMessage(R.string.sms_confirmation_message);
        builder.setPositiveButton(R.string.sms_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.selectedColor = color;
                checkPermissionAndSendSMS(donation);
            }
        });
        builder.setNegativeButton(R.string.sms_no, null);
        builder.show();
    }

    void showThankYouDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String thanks = new String(Character.toChars(0x1F44F)); // Clap clap!
        builder.setTitle(getString(R.string.thank_you_title, thanks));
        builder.setMessage(R.string.thank_you_message);
        builder.setPositiveButton(R.string.understand, null);
        builder.show();
    }

    void showNextPage() {
        final int items = viewPager.getAdapter().getCount();

        if (items - viewPager.getCurrentItem() == 1) {
            // last
            return;
        }
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    private void lightChristmasTree(final ChristmasColor color) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("request");

        databaseReference.addChildEventListener(new FirebaseDatabaseEventAdapter() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

        });

        final LightRequest request = LightRequest.create(color.getTreeColor(), LightRequest.TYPE_DEFINED);

        databaseReference.push().setValue(request);
        showThankYouDialog();
    }

    @Override
    void onSMSSent() {
        lightChristmasTree(selectedColor);
    }

    final static class PagerAdapter extends FragmentPagerAdapter {

        private static int ITEMS = 2;
        private final ChristmasColor startingColor;

        PagerAdapter(FragmentManager fm, final ChristmasColor startingColor) {
            super(fm);
            this.startingColor = startingColor;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return IntroFragment.getInstance(startingColor);
                default:
                    return StreamFragment.getInstance(startingColor);
            }
        }

        @Override
        public int getCount() {
            return ITEMS;
        }
    }
}
