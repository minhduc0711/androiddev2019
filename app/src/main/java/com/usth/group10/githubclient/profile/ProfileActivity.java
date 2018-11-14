package com.usth.group10.githubclient.profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.home.FeedsFragment;
import com.usth.group10.githubclient.others.NothingHereFragment;

public class ProfileActivity extends AppCompatActivity {
    private static final String KEY_USER_URL = "user_url";

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public static Intent newIntent(Context context, String userUrl) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_USER_URL, userUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        mToolbar = findViewById(R.id.toolbar_profile);
        mToolbar.setTitle(R.string.title_activity_profile);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        PagerAdapter adapter = new ProfileFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.view_pager_profile);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(adapter);

        //set header for tab
        mTabLayout = findViewById(R.id.tab_layout_profile);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 7;



        private String titles[] = new String[] { "Overview", "Feed", "Repositories","Starred",
                "Gists","Followers","Following" };
        private ProfileFragmentPagerAdapter(FragmentManager fm) {
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
                case 0: return new OverviewProfileFragment();
                case 1: return new FeedsFragment();
                case 2: return new RepositoriesFragment();
                case 3: return NothingHereFragment.newInstance(titles[page]);
                case 4: return NothingHereFragment.newInstance(titles[page]);
                case 5: return new FollowerFragment();
                case 6: return new FollowingFragment();
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
