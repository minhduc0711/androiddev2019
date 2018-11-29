package com.usth.group10.githubclient.repository;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.NothingHereFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PullRequestsFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public PullRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pull_requests, container, false);

        // set Adapter
        PagerAdapter adapter = new PullRequestsFragment.HomeFragmentPagerAdapter(getChildFragmentManager());
        mViewPager = view.findViewById(R.id.view_pager_pull_requests);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adapter);


        //set header for tab
        mTabLayout = view.findViewById(R.id.tab_layout_pull_requests);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private static class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 2;
        private String titles[] = new String[]{"Opened (0)", "Closed (0)"};

        private HomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        // number of pages for a ViewPager
        @Override
        public androidx.fragment.app.Fragment getItem(int page) {
            // returns an instance of Fragment corresponding to the specified page
            switch (page) {
                case 0:
                    return NothingHereFragment.newInstance("issues");
                case 1:
                    return NothingHereFragment.newInstance("issues");
                default:
                    return new androidx.fragment.app.Fragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }
    }

}
