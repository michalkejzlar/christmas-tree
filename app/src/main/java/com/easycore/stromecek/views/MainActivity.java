package com.easycore.stromecek.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easycore.stromecek.R;

public class MainActivity extends AppCompatActivity {

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

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
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

    final static class PagerAdapter extends FragmentPagerAdapter {

        private static int ITEMS = 2;

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return IntroFragment.getInstance();
                default:
                    return StreamFragment.getInstance(position);
            }
        }

        @Override
        public int getCount() {
            return ITEMS;
        }
    }
}
