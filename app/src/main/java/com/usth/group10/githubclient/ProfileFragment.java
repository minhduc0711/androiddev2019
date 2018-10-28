package com.usth.group10.githubclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // set Adapter
        PagerAdapter adapter = new HomeFragmentPagerAdapter(getChildFragmentManager());
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(adapter);


        //set header for tab
        mTabLayout = view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        return view;
    }


    private static class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 7;
        private String titles[] = new String[] { "Overview", "Feed", "Repositories","Starred",
                "Gists","Followers","Following" };
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
                case 0: return new OverviewProfileFragment();
                case 1: return new ListFragment();
                case 2: return EmptyFragment.newInstance(titles[page]);
                case 3: return EmptyFragment.newInstance(titles[page]);
                case 4: return EmptyFragment.newInstance(titles[page]);
                case 5: return EmptyFragment.newInstance(titles[page]);
                case 6: return EmptyFragment.newInstance(titles[page]);
                default: return new Fragment();
            }
        }
        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }
    }

    public void setPage(int page) {
        mViewPager.setCurrentItem(page);
    }
}


