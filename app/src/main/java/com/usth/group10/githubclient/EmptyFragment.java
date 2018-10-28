package com.usth.group10.githubclient;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment {
    private static final String KEY_ITEM_NAME = "itemName";

    private TextView mTextViewMsgEmpty;

    public static EmptyFragment newInstance(String itemName) {
        
        Bundle args = new Bundle();
        args.putString(KEY_ITEM_NAME, itemName.toLowerCase());
        EmptyFragment fragment = new EmptyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EmptyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empty, container, false);

        String msg = "No" + " " + getArguments().getString(KEY_ITEM_NAME);
        mTextViewMsgEmpty = view.findViewById(R.id.text_no_items);
        mTextViewMsgEmpty.setText(msg);

        return view;
    }
}
