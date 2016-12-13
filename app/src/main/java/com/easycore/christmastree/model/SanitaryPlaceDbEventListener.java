package com.easycore.christmastree.model;


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
        void onPlaceAdded(Donation newPlace);
        void onPlaceChanged(Donation changePlace);
    }

    @Override
    public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
        Log.i("Venue added", dataSnapshot.toString());
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
