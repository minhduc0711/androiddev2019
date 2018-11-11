package com.usth.group10.githubclient.home;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.usth.group10.githubclient.NothingHereFragment;
import com.usth.group10.githubclient.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class IssuesFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public IssuesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issues, container, false);

        // set Adapter
        PagerAdapter adapter = new HomeFragmentPagerAdapter(getChildFragmentManager());
        mViewPager = view.findViewById(R.id.view_pager_issues);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);


        //set header for tab
        mTabLayout = view.findViewById(R.id.tab_layout_issues);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        return view;
    }

    private static class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 4;
        private String titles[] = new String[] {"Created", "Assigned", "Mentioned", "Participated"};
        private HomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
        // number of pages for a ViewPager
        @Override
        public Fragment getItem(int page) {
            // returns an instance of Fragment corresponding to the specified page
            switch (page) {
                case 0: return NothingHereFragment.newInstance("issues");
                case 1: return NothingHereFragment.newInstance("issues");
                case 2: return NothingHereFragment.newInstance("issues");
                case 3: return NothingHereFragment.newInstance("issues");
                default: return new Fragment();
            }
        }
        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }
    }
}
