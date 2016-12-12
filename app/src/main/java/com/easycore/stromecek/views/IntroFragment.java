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

    public static IntroFragment getInstance(int backgroundColor) {
        IntroFragment fr = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt("backgroundColor", backgroundColor);
        fr.setArguments(args);
        return fr;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || getArguments().getInt("backgroundColor", -1) == -1) {
            throw new IllegalArgumentException("You must start this fragment by it's starter methods.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro, container, false);
        ButterKnife.bind(this, view);
        view.setBackgroundColor(getArguments().getInt("backgroundColor"));
        return view;
    }

    @OnClick(R.id.arrowNext)
    public void onNextClicked() {
        ((MainActivity) getActivity()).showNextPage();
    }
}
