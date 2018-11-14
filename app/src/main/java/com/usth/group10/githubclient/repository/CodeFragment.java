package com.usth.group10.githubclient.repository;


import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.usth.group10.githubclient.others.NothingHereFragment;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.repository.codePackage.CommitsFragment;
import com.usth.group10.githubclient.repository.codePackage.ContributorsFragment;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class CodeFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public CodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_code, container, false);

        // set Adapter
        PagerAdapter adapter = new CodeFragment.HomeFragmentPagerAdapter(getChildFragmentManager());
        mViewPager = view.findViewById(R.id.view_pager_code);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(adapter);


        //set header for tab
        mTabLayout = view.findViewById(R.id.tab_layout_code);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        return view;
    }

    private static class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 5;
        private String titles[] = new String[] {"Readme", "Files", "Commits", "Releases", "Contributors"};
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
                case 0: return NothingHereFragment.newInstance("issues");
                case 1: return NothingHereFragment.newInstance("issues");
                case 2: return new CommitsFragment();
                case 3: return NothingHereFragment.newInstance("issues");
                case 4: return new ContributorsFragment();
                default: return new androidx.fragment.app.Fragment();
            }
        }
        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }
    }

}
