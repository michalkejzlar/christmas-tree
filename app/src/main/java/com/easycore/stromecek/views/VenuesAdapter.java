package com.easycore.stromecek.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.easycore.stromecek.R;
import com.easycore.stromecek.model.SanitaryPlace;
import com.easycore.stromecek.utils.ObservableColorMatrix;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.easycore.stromecek.utils.AnimUtils.getFastOutSlowInInterpolator;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 02.12.16.
 */
public class VenuesAdapter  extends RecyclerView.Adapter<VenuesAdapter.VenueViewHolder>{

    private ArrayList<SanitaryPlace> venues;
    private final DatabaseReference mFirebaseDatabaseReference;
    private final Context context;
    private Callback callback;

    public VenuesAdapter(DatabaseReference mFirebaseDatabaseReference, Context context, Callback callback) {
        this.mFirebaseDatabaseReference = mFirebaseDatabaseReference;
        this.context = context;
        this.callback = callback;
        venues = new ArrayList<>();
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("Venue added", dataSnapshot.toString());
                SanitaryPlace venue = new SanitaryPlace();

                if (dataSnapshot.hasChild("name")) {
                    venue.setName(dataSnapshot.child("name").getValue().toString());
                }

                if (dataSnapshot.hasChild("address")) {
                    venue.setAddress(dataSnapshot.child("address").getValue().toString());
                }

                if (dataSnapshot.hasChild("picture")) {
                    venue.setPicture(dataSnapshot.child("picture").getValue().toString());
                }

                if (dataSnapshot.hasChild("dmsNumber")) {
                    venue.setDmsNumber(dataSnapshot.child("dmsNumber").getValue().toString());
                }

                if (dataSnapshot.hasChild("dmsText")) {
                    venue.setDmsText(dataSnapshot.child("dmsText").getValue().toString());
                }

                if (dataSnapshot.hasChild("dmsCost")) {
                    venue.setDmsCost(Double.parseDouble(dataSnapshot.child("dmsCost").getValue().toString()));
                }

                if (dataSnapshot.hasChild("hls")) {
                    venue.setHls(dataSnapshot.child("hls").getValue().toString());
                }

                if (dataSnapshot.hasChild("desc")) {
                    venue.setDsc(dataSnapshot.child("desc").getValue().toString());
                }

                venues.add(venue);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public VenuesAdapter.VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cri_venue, parent, false);
        return new VenueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VenuesAdapter.VenueViewHolder holder, int position) {
        final SanitaryPlace venue = venues.get(position);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onVenueClicked(venue, holder.imvPicture);
            }
        });
        holder.txvName.setText(venue.getName());
        holder.txvAddress.setText(venue.getAddress());


        Glide.with(context)
                .load(venue.getPicture() == null || venue.getPicture().isEmpty() ?
                        null : venue.getPicture())
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(java.lang.Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        holder.imvPicture.setHasTransientState(true);
                        final ObservableColorMatrix cm = new ObservableColorMatrix();
                        final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                                cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                        saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                                () {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                // just animating the color matrix does not invalidate the
                                // drawable so need this update listener.  Also have to create a
                                // new CMCF as the matrix is immutable :(
                                holder.imvPicture.setColorFilter(new ColorMatrixColorFilter(cm));
                            }
                        });
                        saturation.setDuration(2000L);
                        saturation.setInterpolator(getFastOutSlowInInterpolator(context));
                        saturation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.imvPicture.clearColorFilter();
                                holder.imvPicture.setHasTransientState(false);
                            }
                        });
                        saturation.start();
                        return false;
                    }
                })
                .into(holder.imvPicture);


    }

    @Override
    public int getItemCount() {
        return venues.size();
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.venue_container)
        public ViewGroup container;
        @BindView(R.id.venue_txv_name)
        public TextView txvName;
        @BindView(R.id.venue_imv_picture)
        public ImageView imvPicture;
        @BindView(R.id.venue_txv_address)
        public TextView txvAddress;

        public VenueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Callback {
        void onVenueClicked(SanitaryPlace venue, ImageView imageView);
    }
}
