package com.usth.group10.githubclient.profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.usth.group10.githubclient.R;



public class OverviewProfileFragment extends Fragment {
    private ProfileActivity mProfileActivity;
    private Button mFollowersButton;
    private Button mFollowingButton;

    public OverviewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview_profile, container, false);

        mProfileActivity = (ProfileActivity) getActivity();

        mFollowersButton = view.findViewById(R.id.button_profile_followers);
        mFollowersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileActivity.setPage(5);
            }
        });

        mFollowingButton = view.findViewById(R.id.button_profile_following);
        mFollowingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileActivity.setPage(6);
            }
        });

        return view;
    }
}
