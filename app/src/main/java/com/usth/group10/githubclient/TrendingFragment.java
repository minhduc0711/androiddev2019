package com.usth.group10.githubclient;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment {
    LinearLayout itemRepo;

    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        itemRepo = view.findViewById(R.id.item_repo);
        itemRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RepoActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
