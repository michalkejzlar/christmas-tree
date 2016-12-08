package com.easycore.stromecek.model;


import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public final class SanitaryPlaceDbEventListener implements ChildEventListener {

    private final SanitaryEventListener listener;

    public SanitaryPlaceDbEventListener(final SanitaryEventListener listener) {
        this.listener = listener;
    }

    public interface SanitaryEventListener {
        void onPlaceAdded(SanitaryPlace newPlace);
        void onPlaceChanged(SanitaryPlace changePlace);
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
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

        listener.onPlaceAdded(venue);

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

}
