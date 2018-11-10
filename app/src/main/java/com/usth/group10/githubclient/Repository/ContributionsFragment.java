package com.usth.group10.githubclient.Repository;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usth.group10.githubclient.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContributionsFragment extends Fragment {
    public ContributionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contributions, container, false);
    }
}
