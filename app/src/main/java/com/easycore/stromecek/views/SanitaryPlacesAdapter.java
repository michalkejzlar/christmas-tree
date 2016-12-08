package com.easycore.stromecek.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.easycore.stromecek.R;
import com.easycore.stromecek.model.SanitaryPlace;
import com.easycore.stromecek.model.SanitaryPlaceDbEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

final class SanitaryPlacesAdapter extends ArrayAdapter<SanitaryPlace>
        implements SanitaryPlaceDbEventListener.SanitaryEventListener {

    private final ArrayList<SanitaryPlace> places;
    private final DatabaseReference firebaseReference;
    private final LayoutInflater inflater;
    private VenuesAdapter.Callback callback;

    SanitaryPlacesAdapter(final DatabaseReference reference, final Context context) {
        super(context, R.layout.item_sanitary_place, R.id.sanitaryPlaceTextView);
        this.places = new ArrayList<>();
        this.firebaseReference = reference;
        this.inflater = LayoutInflater.from(context);
        final SanitaryPlaceDbEventListener dbListener = new SanitaryPlaceDbEventListener(this);
        firebaseReference.addChildEventListener(dbListener);
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final SanitaryPlace place = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_sanitary_place, parent, false);
        }

        assert place != null;

        ((TextView) convertView).setText(place.getName());

        return convertView;
    }

    @Nullable
    @Override
    public SanitaryPlace getItem(int position) {
        return places.get(position);
    }


    @Override
    public void onPlaceAdded(final SanitaryPlace newPlace) {
        places.add(newPlace);
        notifyDataSetChanged();
    }

    @Override
    public void onPlaceChanged(final SanitaryPlace changePlace) {
        notifyDataSetChanged();
    }
}