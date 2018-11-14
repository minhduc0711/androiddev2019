package com.usth.group10.githubclient.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;
import com.usth.group10.githubclient.others.NothingHereFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    private static final int MAX_RESULTS_COUNT = 20;

    private Toolbar mToolbar;
    private SearchView mSearchView;
    private ViewPager mViewPager;
    private SearchFragmentPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private FrameLayout mProgressBarLayout;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SearchableActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProgressBarLayout = findViewById(R.id.progress_bar_layout_search);

        // set Adapter
        mPagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.view_pager_search);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mPagerAdapter);

        //set header for tab
        mTabLayout = findViewById(R.id.tab_layout_search);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = findViewById(R.id.search_view);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mSearchView.clearFocus();
                mProgressBarLayout.setVisibility(View.VISIBLE);
                performSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void performSearch(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedQuery = "";
            e.printStackTrace();
        }
        String repoSearchUrl = "https://api.github.com/search/repositories?q=" + encodedQuery;
        String userSearchUrl = "https://api.github.com/search/users?q=" + encodedQuery;

        JsonObjectRequest repoRequest = new JsonObjectRequest
                (Request.Method.GET, repoSearchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            switch (response.getInt("total_count")) {
                                case 0:
                                    mPagerAdapter.replaceFragment(NothingHereFragment.newInstance("results"), 0);
                                    break;
                                default:
                                    mPagerAdapter.replaceFragment(
                                            RepoResultsFragment.newInstance(response.getJSONArray("items").toString()), 0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchableActivity.this, "Error getting search results", Toast.LENGTH_SHORT).show();
                    }
                });

        JsonObjectRequest userRequest = new JsonObjectRequest
                (Request.Method.GET, userSearchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            switch (response.getInt("total_count")) {
                                case 0:
                                    mPagerAdapter.replaceFragment(NothingHereFragment.newInstance("results"), 1);
                                    break;
                                default:
                                    mPagerAdapter.replaceFragment(
                                            UserResultsFragment.newInstance(response.getJSONArray("items").toString()), 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mProgressBarLayout.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchableActivity.this, "Error getting search results", Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(repoRequest);
        MySingleton.getInstance(this).addToRequestQueue(userRequest);
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

    private static class SearchFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private final int PAGE_COUNT = 2;
        private String titles[] = new String[] {"Repositories", "Users"};
        private ArrayList<Fragment> mFragmentList = new ArrayList<>();

        private SearchFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i = 0; i < PAGE_COUNT; i++) {
                mFragmentList.add(NothingHereFragment.newInstance("results"));
            }
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
                case 0: return mFragmentList.get(0);
                case 1: return mFragmentList.get(1);
                default: return new Fragment();
            }
        }
        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }

        private void replaceFragment(Fragment fragment, int index) {
            mFragmentList.remove(index);
            mFragmentList.add(index, fragment);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchView.clearFocus();
    }
}
