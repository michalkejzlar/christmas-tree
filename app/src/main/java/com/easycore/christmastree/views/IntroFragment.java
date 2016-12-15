package com.easycore.christmastree.views;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.easycore.christmastree.R;
import com.easycore.christmastree.model.ChristmasColor;

public class IntroFragment extends Fragment {

    public static IntroFragment getInstance(final ChristmasColor backgroundColor) {
        IntroFragment fr = new IntroFragment();
        Bundle args = new Bundle();
        args.putParcelable("backgroundColor", backgroundColor);
        fr.setArguments(args);
        return fr;
    }

    @BindView(R.id.introTextView)
    protected TextView introTextView;

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
        if (getArguments() == null || getArguments().getParcelable("backgroundColor") == null) {
            throw new IllegalArgumentException("You must start this fragment by it's starter methods.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro, container, false);
        ButterKnife.bind(this, view);
        final ChristmasColor color = getArguments().getParcelable("backgroundColor");
        assert color != null; // check earlier in onCreate()
        view.setBackgroundColor(color.getMaterialColor());

        final String treeEmoji = new String(Character.toChars(0x1F384)); // Treeeees!
        final String presentEmoji = new String(Character.toChars(0x1F381)); // Giiiifts!
        introTextView.setText(getString(R.string.intro_text, treeEmoji,
                treeEmoji, treeEmoji, presentEmoji));

        return view;
    }

    @OnClick(R.id.arrowNext)
    public void onNextClicked() {
        ((MainActivity) getActivity()).showNextPage();
    }
}
