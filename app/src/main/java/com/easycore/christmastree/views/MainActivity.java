package com.easycore.christmastree.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easycore.christmastree.R;
import com.easycore.christmastree.model.ChristmasColor;
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showNextPage() {
        final int items = viewPager.getAdapter().getCount();

        if (items - viewPager.getCurrentItem() == 1) {
            // last
            return;
        }
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    void lightChristmasTree(final ChristmasColor color) {
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
