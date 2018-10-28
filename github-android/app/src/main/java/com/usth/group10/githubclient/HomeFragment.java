package com.usth.group10.githubclient;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private BottomNavigationView mBottomNavigationView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mBottomNavigationView = view.findViewById(R.id.bottom_nav_home);
        getChildFragmentManager().beginTransaction().add(R.id.layout_home, new ListFragment()).commit();
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment newFragment;
                switch (menuItem.getItemId()) {
                    case R.id.item_bottom_nav_feeds:
                        Log.d(TAG, "Fragment for feeds created");
                        newFragment = new ListFragment();
                        break;
                    case R.id.item_bottom_nav_issues:
                        newFragment = new IssuesFragment();
                        break;
                    case R.id.item_bottom_nav_pull_requests:
                        newFragment = EmptyFragment.newInstance(getResources().getString(R.string.bottom_nav_pull_requests));
                        break;
                    default:
                        newFragment = new Fragment();
                }
                getChildFragmentManager().beginTransaction().replace(R.id.layout_home, newFragment).commit();
                return true;
            }
        });
        mBottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                // Do nothing
            }
        });

        return view;
    }

}
