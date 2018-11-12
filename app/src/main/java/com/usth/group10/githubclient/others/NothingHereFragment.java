package com.usth.group10.githubclient.others;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usth.group10.githubclient.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NothingHereFragment extends Fragment {
    private static final String KEY_ITEM_NAME = "itemName";

    private TextView mTextViewMsgEmpty;

    public static NothingHereFragment newInstance(String itemName) {
        
        Bundle args = new Bundle();
        args.putString(KEY_ITEM_NAME, itemName.toLowerCase());
        NothingHereFragment fragment = new NothingHereFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public NothingHereFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nothing_here, container, false);

        String msg = "No" + " " + getArguments().getString(KEY_ITEM_NAME);
        mTextViewMsgEmpty = view.findViewById(R.id.text_no_items);
        mTextViewMsgEmpty.setText(msg);

        return view;
    }
}
