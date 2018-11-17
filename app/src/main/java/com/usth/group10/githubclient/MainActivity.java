package com.usth.group10.githubclient;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.usth.group10.githubclient.home.HomeFragment;
import com.usth.group10.githubclient.others.MySingleton;
import com.usth.group10.githubclient.profile.ProfileActivity;
import com.usth.group10.githubclient.search.SearchableActivity;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private Toolbar mMainToolbar;
    private NavigationView mNavigationView;

    private View mNavHeader;
    private CircleImageView mUserAvatarImageView;
    private TextView mNameTextView;
    private TextView mUserNameTextView;

    private int mCurrentSelectedItemResId;
    private String mAuthenticatedUserUrl;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mMainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mMainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);

        mNavigationView = findViewById(R.id.nav_view_main);

        mNavHeader = mNavigationView.getHeaderView(0);
        mUserAvatarImageView = mNavHeader.findViewById(R.id.image_user_avatar_nav_header);
        mNameTextView = mNavHeader.findViewById(R.id.text_name_nav_header);
        mUserNameTextView = mNavHeader.findViewById(R.id.text_username_nav_header);
        updateNavHeader();

        mNavigationView.setCheckedItem(R.id.item_drawer_home);
        mCurrentSelectedItemResId = R.id.item_drawer_home;
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, new HomeFragment()).commit();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                // Swap fragments if switch to new item
                if (menuItem.getItemId() != mCurrentSelectedItemResId) {
                    Intent intent;
                    switch (menuItem.getItemId()) {
                        case R.id.item_drawer_home:
                            Log.d(TAG, "Home fragment created");
                            mCurrentSelectedItemResId = menuItem.getItemId();
                            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, new HomeFragment()).commit();
                            mMainToolbar.setTitle(R.string.app_name);
                            break;
                        case R.id.item_drawer_profile:
                            intent = ProfileActivity.newIntent(MainActivity.this, mAuthenticatedUserUrl);
                            startActivity(intent);
                            break;
                        case R.id.item_drawer_trending:
                            mCurrentSelectedItemResId = menuItem.getItemId();
                            mMainToolbar.setTitle(menuItem.getTitle());
                            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, new TrendingFragment()).commit();
                            break;
                        case R.id.item_drawer_logout:
                            finish();
                            SharedPreferences.Editor editor = getSharedPreferences(MySingleton.PREF_LOGIN_INFO, MODE_PRIVATE).edit();
                            editor.clear().apply();
                            intent = LoginActivity.newIntent(MainActivity.this);
                            startActivity(intent);
                        default:
                            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, new Fragment()).commit();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.item_action_search:
                Intent intent = SearchableActivity.newIntent(this);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNavHeader() {
        String access_token = getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        String url = "https://api.github.com/user?access_token=" + access_token;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mAuthenticatedUserUrl = response.getString("url");
                            Picasso.get().load(response.getString("avatar_url")).into(mUserAvatarImageView);
                            if (!response.getString("name").equals("null")) {
                                mNameTextView.setText(response.getString("name"));
                            } else {
                                mNameTextView.setVisibility(View.GONE);
                            }
                            mUserNameTextView.setText(response.getString("login"));

                            SharedPreferences.Editor editor = getSharedPreferences(MySingleton.PREF_LOGIN_INFO, MODE_PRIVATE).edit();
                            editor.putString(MySingleton.KEY_USERNAME, response.getString("login"));
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Loading user info failed", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
