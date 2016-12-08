package com.easycore.stromecek.views;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easycore.stromecek.R;

public class IntroFragment extends Fragment {

    public static IntroFragment getInstance() {
        return new IntroFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            MainActivity main = (MainActivity) activity;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("This fragment must be child of MainActivity!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.arrowNext)
    public void onNextClicked() {
        ((MainActivity) getActivity()).showNextPage();
    }
}
