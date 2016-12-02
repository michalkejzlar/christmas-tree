package com.easycore.stromecek.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.easycore.stromecek.R;
import com.easycore.stromecek.model.Venue;
import com.easycore.stromecek.views.activities.VenueDetailActivity;
import com.easycore.stromecek.views.adapters.VenuesAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements VenuesAdapter.Callback {

    @BindView(R.id.venues_rcv)
    protected RecyclerView rcv;
    private LinearLayoutManager mLinearLayoutManager;
    private VenuesAdapter adapter;
    private DatabaseReference mFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("venues");
        adapter = new VenuesAdapter(mFirebaseDatabaseReference, this, this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(mLinearLayoutManager);
        rcv.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onVenueClicked(Venue venue, ImageView imageView) {
        Intent intent = new Intent(this, VenueDetailActivity.class);
        intent.putExtra("venue", venue);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) imageView, "picture");
        startActivity(intent, options.toBundle());
    }
}
